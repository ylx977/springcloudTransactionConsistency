package com.fuzamei.service.impl;

import com.fuzamei.annotations.TX;
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
 * Created by ylx on 2018/12/25.
 */
@Slf4j
@Service
public class BserviceImpl implements Bservice {

    private final Bmapper bmapper;
    private final DataSource dataSource;
    private final DataSourceTransactionManager dataSourceTransactionManager;

    public BserviceImpl(Bmapper amapper,
                        DataSource dataSource,
                        DataSourceTransactionManager dataSourceTransactionManager) {
        this.bmapper = amapper;
        this.dataSource = dataSource;
        this.dataSourceTransactionManager = dataSourceTransactionManager;
    }

    @Override
    @TX(serviceName = "SERVICEB")
    public boolean updateMoneyb(String id, Double moneys,String groupId) {
        long time = System.currentTimeMillis();
        int i = bmapper.updateMoneyb(id, moneys, time);
        log.info(i+"");
//        int x = 1/0;
        return i > 0;
    }
}
