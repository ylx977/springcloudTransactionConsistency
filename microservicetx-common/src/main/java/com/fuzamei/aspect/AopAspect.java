package com.fuzamei.aspect;

import com.fuzamei.annotations.TX;
import com.fuzamei.managerClient.TxManagerClient;
import com.fuzamei.txclient.TxClient;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.stereotype.Component;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * Created by ylx on 2018/12/14.
 */
@Aspect
@Component
@Slf4j
public class AopAspect {

    private final DataSourceTransactionManager dataSourceTransactionManager;
    private final TxManagerClient txManagerClient;
    private final DataSource dataSource;
    @Autowired
    public AopAspect(DataSourceTransactionManager dataSourceTransactionManager,
                     TxManagerClient txManagerClient,
                     DataSource dataSource) {
        this.dataSourceTransactionManager = dataSourceTransactionManager;
        this.txManagerClient = txManagerClient;
        this.dataSource = dataSource;
    }

    @Pointcut(value = "@annotation(tx)")
    public void serviceStatistics(TX tx) {
    }

    @Around(value = "serviceStatistics(tx)")
    public Object doAround(ProceedingJoinPoint joinPoint, TX tx) throws SQLException {
        log.info("环绕通知之开始");
        String serviceName = tx.serviceName();
        Object[] args = joinPoint.getArgs();
        String groupId = (String) args[2];

        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        // 事物隔离级别，开启新事务，这样会比较安全些。
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
        // 获得事务状态
        TransactionStatus status = dataSourceTransactionManager.getTransaction(def);
        Connection connection = DataSourceUtils.getConnection(dataSource);
        connection.setAutoCommit(false);
        try {
            Object proceed = joinPoint.proceed();
            if(proceed.equals(Boolean.TRUE)){
                txManagerClient.createTxGroup(groupId,serviceName,"OK");
            }else{
                txManagerClient.createTxGroup(groupId,serviceName,"NO");
            }
            return proceed;
        } catch (Throwable e) {
            log.error("修改数据库出现异常，回滚数据");
            e.printStackTrace();
            txManagerClient.createTxGroup(groupId,serviceName,"NO");
            return false;
        }finally {
            TxClient.putConnection(groupId,connection);
            log.info("环绕通知之结束");
        }
    }

    @AfterReturning(value = "serviceStatistics(tx)")
    public void doAfterReturning(JoinPoint joinPoint, TX tx){
        if(tx.initial()){
            log.info("最后处理所有事务操作");
            Object[] args = joinPoint.getArgs();
            String groupId = (String) args[2];
            txManagerClient.judgeTx(groupId);
        }
    }


//    @Around(value = "serviceStatistics(tx)")
//    public Object doAround(ProceedingJoinPoint joinPoint, TX tx) throws SQLException {
//        log.info("环绕通知之开始");
//        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
//        // 事物隔离级别，开启新事务，这样会比较安全些。
//        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
//        // 获得事务状态
//        TransactionStatus status = dataSourceTransactionManager.getTransaction(def);
//        try {
//            Object proceed = joinPoint.proceed();
//            dataSourceTransactionManager.commit(status);
//            return proceed;
//        } catch (Throwable e) {
//            log.error("修改数据库出现异常，回滚数据");
//            e.printStackTrace();
//            dataSourceTransactionManager.rollback(status);
//            return 0;
//        }finally {
//            log.info("环绕通知之结束");
//        }
//    }

}
