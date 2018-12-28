package com.fuzamei.service.impl;

import com.fuzamei.clients.BserviceClient;
import com.fuzamei.clients.CserviceClient;
import com.fuzamei.enums.ResponseEnum;
import com.fuzamei.managerClient.TxManagerClient;
import com.fuzamei.service.NotifyService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * Created by ylx on 2018/12/27.
 */
@Slf4j
@Service
public class NotifyServiceImpl implements NotifyService {

    private final BserviceClient bserviceClient;
    private final CserviceClient cserviceClient;
    private final TxManagerClient txManagerClient;

    @Autowired
    public NotifyServiceImpl(BserviceClient bserviceClient,
                             CserviceClient cserviceClient,
                             TxManagerClient txManagerClient) {
        this.bserviceClient = bserviceClient;
        this.cserviceClient = cserviceClient;
        this.txManagerClient = txManagerClient;
    }


    @Async
    @Override
    public void distributeUpdateb(String id, String moneys, String groupId) {
        //b服务返回的结果
        String bresult;
        try {
            bresult = bserviceClient.distributeUpdate(id, moneys, groupId);
        }catch (Exception e){
            e.printStackTrace();
            log.error("调用b服务出现异常"+e.getMessage());
            return;
        }
        if(ResponseEnum.FAIL.getName().equals(bresult)){
            log.info("异步调用b服务失败");
        }else{
            log.info("异步调用b服务成功");
        }
    }

    @Async
    @Override
    public void judgeTx(String groupId, Integer count) {
        //c服务返回的结果
        String cresult;
        try {
            cresult = txManagerClient.judgeTx(groupId, String.valueOf(count));
        }catch (Exception e){
            e.printStackTrace();
            log.error("调用txmanager服务出现异常"+e.getMessage());
            return;
        }
        if(ResponseEnum.FAIL.getName().equals(cresult)){
            log.info("异步调用txmanager服务失败");
        }else{
            log.info("异步调用txmanager服务成功");
        }
    }

    @Async
    @Override
    public void distributeUpdatec(String id, String moneys, String groupId) {
        //c服务返回的结果
        String cresult;
        try {
            cresult = cserviceClient.distributeUpdate(id, moneys,groupId);
        }catch (Exception e){
            e.printStackTrace();
            log.error("调用c服务出现异常"+e.getMessage());
            return;
        }
        if(ResponseEnum.FAIL.getName().equals(cresult)){
            log.info("异步调用c服务失败");
        }else{
            log.info("异步调用c服务成功");
        }
    }
}
