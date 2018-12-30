package com.fuzamei.controller;

import com.fuzamei.constants.RedisPrefix;
import com.fuzamei.enums.ResponseEnum;
import com.fuzamei.enums.TypeEnum;
import com.fuzamei.utils.RedisUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * @author ylx
 * Created by ylx on 2018/12/26.
 */
@Slf4j
@RestController
@RequestMapping("/txmanage")
public class TxManagerController {

    private final RedisUtils redisUtils;

    @Autowired
    public TxManagerController(RedisUtils redisUtils) {
        this.redisUtils = redisUtils;
    }

    /**
     * 创建事务组标识，接收来自各个服务的事务挂起后接收事务成功还是失败的接口
     * @param groupId   事务组id号
     * @param serviceName   服务名
     * @param type OK表示事务成功 NO表示事务失败
     * @return
     */
    @PostMapping("/createTxGroup/{groupId}/{serviceName}/{type}")
    public String createOKTxGroup(@PathVariable(value = "groupId") String groupId,
                                @PathVariable(value = "serviceName") String serviceName,
                                @PathVariable(value = "type") String type){
        //将每个服务的事务状态存入redis中
        boolean create = redisUtils.hset(RedisPrefix.TX_GROUP + groupId, serviceName, type);
        return create ? ResponseEnum.SUCCESS.getName() : ResponseEnum.FAIL.getName();
    }

    /**
     * 由事务发起方最终调用txManager这个接口，并最终判断是否对各个挂起的事务进行提交还是回滚
     * @param groupId   事务组id号
     * @return SUCCESS表示事务是成功的，可以提交    FAIL表示失败，进行回滚
     */
    @PostMapping("/judgeTx/{groupId}/{count}")
    public String judgeTx(@PathVariable(value = "groupId") String groupId,
                          @PathVariable(value = "count") String count){
        int serviceCount = Integer.parseInt(count);
        int index  = 0;
        Map<String, String> map;
        //初始线程等待时间10ms
        long initialWait = 10;
        //最大等待时间
        long totalWait = 6000;
        //累积等待时间
        long sumWait = 0;
        while(true){
            map = redisUtils.hgetAll(RedisPrefix.TX_GROUP + groupId);
            if(map.size() == 0){
                //如果没有查到说明事务组根本不存在，直接返回失败
                return ResponseEnum.FAIL.getName();
            }
            log.info("查询redis{}次,当前需要等待时间{}ms,累计等待时间{}ms",index+1,initialWait,sumWait);
            if(map.size() < serviceCount){
                index++;
            } else if(map.size() == serviceCount){
                break;
            }

            for(Map.Entry<String,String> entry : map.entrySet()){
                if(TypeEnum.NO.getName().equals(entry.getValue())){
                    //只要有服务有一个是失败的直接通知所有服务回滚事务
                    log.info("检测到有服务的事务已经失败,直接通知回滚事务");
                    return ResponseEnum.FAIL.getName();
                }
            }

            //累计时间超时未获取满通知所有带事务的微服务挂了
            if(sumWait > totalWait){
                //如果超时还未获取全
                log.info("tx-manager超时还未获取全所有事务信息");
                return ResponseEnum.FAIL.getName();
            }

            try {
                //每次等待的时间都要加倍, 否则访问redis的轮询次数太多
                Thread.sleep(initialWait);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            //防止同频率反复查询redis，时间扩容因子
            initialWait *= 1.5;
            sumWait += initialWait;
        }

        boolean flag = true;
        for(Map.Entry<String,String> entry : map.entrySet()){
            if(TypeEnum.NO.getName().equals(entry.getValue())){
                flag = false;
                break;
            }
        }

        return flag ? ResponseEnum.SUCCESS.getName() : ResponseEnum.FAIL.getName();
    }

}
