package com.fuzamei.service.impl;

import com.fuzamei.annotations.TX;
import com.fuzamei.constants.ServiceName;
import com.fuzamei.mapper.Cmapper;
import com.fuzamei.service.Cservice;
import org.springframework.stereotype.Service;

/**
 * Created by ylx on 2018/12/25.
 */
@Service
public class CserviceImpl implements Cservice {

    private final Cmapper cmapper;

    public CserviceImpl(Cmapper amapper) {
        this.cmapper = amapper;
    }

    @Override
    @TX(serviceName = ServiceName.SERVICE_C)
    public boolean updateMoneyc(String id, Double moneys, String groupId) {
        long time = System.currentTimeMillis();
        int i = cmapper.updateMoneyc(id, moneys, time);
        return i > 0;
    }
}
