package com.fuzamei.service.impl;

import com.fuzamei.clients.BserviceClient;
import com.fuzamei.clients.CserviceClient;
import com.fuzamei.annotations.TX;
import com.fuzamei.mapper.Amapper;
import com.fuzamei.service.Aservice;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Created by ylx on 2018/12/25.
 */
@Slf4j
@Service
public class AserviceImpl implements Aservice{

    private final Amapper amapper;
    private final BserviceClient bserviceClient;
    private final CserviceClient cserviceClient;

    public AserviceImpl(Amapper amapper,
                        BserviceClient bserviceClient,
                        CserviceClient cserviceClient) {
        this.amapper = amapper;
        this.bserviceClient = bserviceClient;
        this.cserviceClient = cserviceClient;
    }

    @Override
    @TX(initial = true,serviceName = "SERVICEA")
    public boolean updateMoneya(String id, Double moneys, String groupId) {
        long time = System.currentTimeMillis();
        int i = amapper.updateMoneya(id, moneys, time);
        if(i == 0){
            return false;
        }

        String bresult;
        try {
            bresult = bserviceClient.distributeUpdate(id, String.valueOf(moneys), groupId);
        }catch (Exception e){
            log.error("调用serviceb出现异常"+e.getMessage());
            return false;
        }
        if("fail".equals(bresult)){
            return false;
        }


        String cresult;
        try {
            cresult = cserviceClient.distributeUpdate(id, String.valueOf(moneys),groupId);
        }catch (Exception e){
            log.error("调用servicec出现异常"+e.getMessage());
            return false;
        }
        if("fail".equals(cresult)){
            return false;
        }

        return true;
    }
}
