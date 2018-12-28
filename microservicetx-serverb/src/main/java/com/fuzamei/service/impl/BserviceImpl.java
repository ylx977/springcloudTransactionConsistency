package com.fuzamei.service.impl;

import com.fuzamei.annotations.TX;
import com.fuzamei.constants.ServiceName;
import com.fuzamei.mapper.Bmapper;
import com.fuzamei.service.Bservice;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.stereotype.Service;
import sun.rmi.runtime.Log;

import javax.sql.DataSource;
import java.sql.SQLException;

/**
 * @author ylx
 * Created by ylx on 2018/12/25.
 */
@Slf4j
@Service
public class BserviceImpl implements Bservice {

    private final Bmapper bmapper;

    public BserviceImpl(Bmapper amapper) {
        this.bmapper = amapper;
    }

    @Override
    @TX(serviceName = ServiceName.SERVICE_B)
    public boolean updateMoneyb(String id, Double moneys,String groupId) {
        long time = System.currentTimeMillis();
        int i = bmapper.updateMoneyb(id, moneys, time);
        log.info(i+"");
        return i > 0;
    }
}
