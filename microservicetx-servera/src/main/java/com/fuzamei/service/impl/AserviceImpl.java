package com.fuzamei.service.impl;

import com.fuzamei.clients.BserviceClient;
import com.fuzamei.clients.CserviceClient;
import com.fuzamei.annotations.TX;
import com.fuzamei.constants.ServiceName;
import com.fuzamei.enums.ResponseEnum;
import com.fuzamei.mapper.Amapper;
import com.fuzamei.service.Aservice;
import com.fuzamei.service.NotifyService;
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
    private final NotifyService notifyService;

    public AserviceImpl(Amapper amapper,
                        BserviceClient bserviceClient,
                        CserviceClient cserviceClient,
                        NotifyService notifyService) {
        this.amapper = amapper;
        this.bserviceClient = bserviceClient;
        this.cserviceClient = cserviceClient;
        this.notifyService = notifyService;
    }

    @Override
    @TX(initial = true, serviceName = ServiceName.SERVICE_A, serviceCount = 3)
    public boolean updateMoneya(String id, Double moneys, String groupId) {
        long time = System.currentTimeMillis();
        int i = amapper.updateMoneya(id, moneys, time);
        if(i == 0){
            return false;
        }

        //异步通知b服务更新
        notifyService.distributeUpdateb(id, String.valueOf(moneys), groupId);
        //异步通知c服务更新
        notifyService.distributeUpdatec(id, String.valueOf(moneys), groupId);

        return true;
    }
}
