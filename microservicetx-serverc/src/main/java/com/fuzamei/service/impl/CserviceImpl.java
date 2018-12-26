package com.fuzamei.service.impl;

import com.fuzamei.annotations.TX;
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
    @TX(serviceName = "SERVICEC")
    public boolean updateMoneyb(String id, Double moneys, String groupId) {
        long time = System.currentTimeMillis();
        int i = cmapper.updateMoneyc(id, moneys, time);
        int x = 1/0;
        return i > 0;
    }
}
