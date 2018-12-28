package com.fuzamei.controller;

import com.fuzamei.constants.RedisPrefix;
import com.fuzamei.constants.ServiceName;
import com.fuzamei.enums.ResponseEnum;
import com.fuzamei.enums.TypeEnum;
import com.fuzamei.txClient.*;
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
    private final ServiceaClient serviceaClient;
    private final ServicebClient servicebClient;
    private final ServicecClient servicecClient;
    private final ServicedClient servicedClient;
    private final ServiceeClient serviceeClient;

    @Autowired
    public TxManagerController(RedisUtils redisUtils,
                               ServiceaClient serviceaClient,
                               ServicebClient servicebClient,
                               ServicecClient servicecClient,
                               ServicedClient servicedClient,
                               ServiceeClient serviceeClient) {
        this.redisUtils = redisUtils;
        this.serviceaClient = serviceaClient;
        this.servicebClient = servicebClient;
        this.servicecClient = servicecClient;
        this.servicedClient = servicedClient;
        this.serviceeClient = serviceeClient;
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
     * @return
     */
    @PostMapping("/judgeTx/{groupId}/{count}")
    public String judgeTx(@PathVariable(value = "groupId") String groupId,
                          @PathVariable(value = "count") String count){
        int serviceCount = Integer.parseInt(count);
        int index  = 0;
        Map<String, String> map;
        while(true){
            if(index > 500){
                return ResponseEnum.SUCCESS.getName();
            }
            map = redisUtils.hgetAll(RedisPrefix.TX_GROUP + groupId);
            if(map.size() < serviceCount){
                index++;
            } else if(map.size() == serviceCount){
                break;
            } else{
                //如果超时还未获取全
                sendTypeToService(map,groupId,TypeEnum.NO.getName());
                return ResponseEnum.SUCCESS.getName();
            }
            try {
                Thread.sleep(20);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        boolean flag = true;
        for(Map.Entry<String,String> entry : map.entrySet()){
            if(TypeEnum.NO.getName().equals(entry.getValue())){
                flag = false;
                break;
            }
        }

        if(flag){
            //说明所有事务都成功了，全部通知提交事务
            sendTypeToService(map,groupId,TypeEnum.OK.getName());
        }else{
            //说明有部分事务失败了，全部通知回滚
            sendTypeToService(map,groupId,TypeEnum.NO.getName());
        }
        return ResponseEnum.SUCCESS.getName();
    }


    /**
     * 给A,B,C服务通知到底是成功还是失败
     */
    private void sendTypeToService(Map<String, String> map, String groupId, String type){
        for(Map.Entry<String,String> entry : map.entrySet()){
            if(ServiceName.SERVICE_A.equals(entry.getKey())){
                serviceaClient.decideConnection(groupId,type);
            }
            if(ServiceName.SERVICE_B.equals(entry.getKey())){
                servicebClient.decideConnection(groupId,type);
            }
            if(ServiceName.SERVICE_C.equals(entry.getKey())){
                servicecClient.decideConnection(groupId,type);
            }
            if(ServiceName.SERVICE_D.equals(entry.getKey())){
                servicedClient.decideConnection(groupId,type);
            }
            if(ServiceName.SERVICE_E.equals(entry.getKey())){
                serviceeClient.decideConnection(groupId,type);
            }
        }
    }


}
