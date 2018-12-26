package com.fuzamei.controller;

import com.fuzamei.txClient.ServiceaClient;
import com.fuzamei.txClient.ServicebClient;
import com.fuzamei.txClient.ServicecClient;
import com.fuzamei.utils.RedisUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Created by ylx on 2018/12/26.
 */
@Slf4j
@RestController
@RequestMapping("/txmanage")
public class TxManagerController {

    @Autowired
    private RedisUtils redisUtils;
    @Autowired
    private ServiceaClient serviceaClient;
    @Autowired
    private ServicebClient servicebClient;
    @Autowired
    private ServicecClient servicecClient;

    /**
     * 创建事务组标识
     * @param groupId
     * @param serviceName
     * @param type OK表示事务成功 NO表示事务失败
     * @return
     */
    @PostMapping("/createTxGroup/{groupId}/{serviceName}/{type}")
    public String createOKTxGroup(@PathVariable(value = "groupId") String groupId,
                                @PathVariable(value = "serviceName") String serviceName,
                                @PathVariable(value = "type") String type){
        boolean create = redisUtils.hset("TX_GROUP:" + groupId, serviceName, type);
        return create ? "success" : "false";
    }


    @PostMapping("/judgeTx/{groupId}")
    public String judgeTx(@PathVariable(value = "groupId") String groupId){
        Map<String, String> map = redisUtils.hgetAll("TX_GROUP:" + groupId);
        if(map.size() == 0){
            return "success";
        }

        boolean flag = true;
        for(Map.Entry<String,String> entry : map.entrySet()){
            if("NO".equals(entry.getValue())){
                flag = false;
                break;
            }
        }

        if(flag){
            //说明所有事务都成功了，全部通知提交事务
            for(Map.Entry<String,String> entry : map.entrySet()){
                if("SERVICEA".equals(entry.getKey())){
                    serviceaClient.decideConnection(groupId,"OK");
                }
                if("SERVICEB".equals(entry.getKey())){
                    servicebClient.decideConnection(groupId,"OK");
                }
                if("SERVICEC".equals(entry.getKey())){
                    servicecClient.decideConnection(groupId,"OK");
                }
            }
        }else{
            //说明有部分事务失败了，全部通知回滚
            for(Map.Entry<String,String> entry : map.entrySet()){
                if("SERVICEA".equals(entry.getKey())){
                    serviceaClient.decideConnection(groupId,"NO");
                }
                if("SERVICEB".equals(entry.getKey())){
                    servicebClient.decideConnection(groupId,"NO");
                }
                if("SERVICEC".equals(entry.getKey())){
                    servicecClient.decideConnection(groupId,"NO");
                }
            }
        }
        return "success";

    }


}
