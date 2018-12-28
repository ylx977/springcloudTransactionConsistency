package com.fuzamei.service.impl;

import com.fuzamei.clients.DserviceClient;
import com.fuzamei.clients.EserviceClient;
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

    private final DserviceClient dserviceClient;
    private final EserviceClient eserviceClient;
    private final TxManagerClient txManagerClient;

    @Autowired
    public NotifyServiceImpl(DserviceClient dserviceClient,
                             EserviceClient eserviceClient,
                             TxManagerClient txManagerClient) {
        this.dserviceClient = dserviceClient;
        this.eserviceClient = eserviceClient;
        this.txManagerClient = txManagerClient;
    }


    @Async
    @Override
    public void distributeUpdated(String id, String moneys, String groupId) {
        //b服务返回的结果
        String bresult;
        try {
            bresult = dserviceClient.distributeUpdate(id, moneys, groupId);
        }catch (Exception e){
            e.printStackTrace();
            log.error("调用d服务出现异常"+e.getMessage());
            return;
        }
        if(ResponseEnum.FAIL.getName().equals(bresult)){
            log.info("异步调用d服务失败");
        }else{
            log.info("异步调用d服务成功");
        }
    }

    @Async
    @Override
    public void distributeUpdatee(String id, String moneys, String groupId) {
        //c服务返回的结果
        String cresult;
        try {
            cresult = eserviceClient.distributeUpdate(id, moneys,groupId);
        }catch (Exception e){
            e.printStackTrace();
            log.error("调用e服务出现异常"+e.getMessage());
            return;
        }
        if(ResponseEnum.FAIL.getName().equals(cresult)){
            log.info("异步调用e服务失败");
        }else{
            log.info("异步调用e服务成功");
        }
    }
}
