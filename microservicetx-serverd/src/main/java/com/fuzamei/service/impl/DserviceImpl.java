package com.fuzamei.service.impl;

import com.fuzamei.annotations.TX;
import com.fuzamei.constants.ServiceName;
import com.fuzamei.mapper.Dmapper;
import com.fuzamei.service.Dservice;
import org.springframework.stereotype.Service;

/**
 * @author ylx
 * Created by ylx on 2018/12/25.
 */
@Service
public class DserviceImpl implements Dservice {

    private final Dmapper dmapper;

    public DserviceImpl(Dmapper dmapper) {
        this.dmapper = dmapper;
    }

    @Override
    @TX(serviceName = ServiceName.SERVICE_D)
    public boolean updateMoneyd(String id, Double moneys, String groupId) {
        long time = System.currentTimeMillis();
        int i = dmapper.updateMoneyd(id, moneys, time);
        return i > 0;
    }
}
