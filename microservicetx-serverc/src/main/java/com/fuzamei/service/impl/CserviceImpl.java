package com.fuzamei.service.impl;

import com.fuzamei.annotations.TX;
import com.fuzamei.constants.ServiceName;
import com.fuzamei.mapper.Cmapper;
import com.fuzamei.service.Cservice;
import com.fuzamei.service.NotifyService;
import org.springframework.stereotype.Service;

/**
 * @author ylx
 * Created by ylx on 2018/12/25.
 */
@Service
public class CserviceImpl implements Cservice {

    private final Cmapper cmapper;
    private final NotifyService notifyService;

    public CserviceImpl(Cmapper amapper,
                        NotifyService notifyService) {
        this.cmapper = amapper;
        this.notifyService = notifyService;
    }

    @Override
    @TX(serviceName = ServiceName.SERVICE_C)
    public boolean updateMoneyc(String id, Double moneys, String groupId) {
        long time = System.currentTimeMillis();
        int i = cmapper.updateMoneyc(id, moneys, time);

        if(i == 0){
            return false;
        }

        //异步发送给服务d
        notifyService.distributeUpdated(id,String.valueOf(moneys),groupId);
        //异步发送给服务e
        notifyService.distributeUpdatee(id,String.valueOf(moneys),groupId);

        return true;
    }
}
