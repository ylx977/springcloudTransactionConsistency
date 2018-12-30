package com.fuzamei.service.impl;

import com.fuzamei.annotations.TX;
import com.fuzamei.constants.ServiceName;
import com.fuzamei.mapper.Emapper;
import com.fuzamei.service.Eservice;
import org.springframework.stereotype.Service;

/**
 * @author ylx
 * Created by ylx on 2018/12/25.
 */
@Service
public class EserviceImpl implements Eservice {

    private final Emapper emapper;

    public EserviceImpl(Emapper emapper) {
        this.emapper = emapper;
    }

    @Override
    @TX(serviceName = ServiceName.SERVICE_E,serviceCount = 5)
    public boolean updateMoneye(String id, Double moneys, String groupId) {
        long time = System.currentTimeMillis();
        int i = emapper.updateMoneye(id, moneys, time);
        if(i == 0){
            return false;
        }

//        int x = 1/0;

        //todo 可做其它业务或调用其它微服务的事务

        return true;
    }
}
