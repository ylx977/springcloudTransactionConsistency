package com.fuzamei.service.impl;

import com.fuzamei.annotations.TX;
import com.fuzamei.constants.ServiceName;
import com.fuzamei.mapper.Emapper;
import com.fuzamei.service.Eservice;
import org.springframework.stereotype.Service;

/**
 * Created by ylx on 2018/12/25.
 */
@Service
public class EserviceImpl implements Eservice {

    private final Emapper emapper;

    public EserviceImpl(Emapper emapper) {
        this.emapper = emapper;
    }

    @Override
    @TX(serviceName = ServiceName.SERVICE_E)
    public boolean updateMoneye(String id, Double moneys, String groupId) {
        long time = System.currentTimeMillis();
        int i = emapper.updateMoneye(id, moneys, time);
        return i > 0;
    }
}
