package com.fuzamei.aspect;

import com.fuzamei.annotations.TX;
import com.fuzamei.constants.TimeOut;
import com.fuzamei.enums.TypeEnum;
import com.fuzamei.managerClient.TxManagerClient;
import com.fuzamei.txclient.TxClient;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.stereotype.Component;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import java.sql.SQLException;
import java.util.concurrent.Exchanger;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * @author ylx
 * Created by ylx on 2018/12/14.
 */
@Aspect
@Component
@Slf4j
public class AopAspect {

    private final DataSourceTransactionManager dataSourceTransactionManager;
    private final TxManagerClient txManagerClient;
    private final ExecutorService executorService;

    @Autowired
    public AopAspect(DataSourceTransactionManager dataSourceTransactionManager,
                     TxManagerClient txManagerClient,
                     ExecutorService executorService) {
        this.dataSourceTransactionManager = dataSourceTransactionManager;
        this.txManagerClient = txManagerClient;
        this.executorService = executorService;
    }

    @Pointcut(value = "@annotation(tx)")
    public void serviceStatistics(TX tx) {
    }

    @Around(value = "serviceStatistics(tx)")
    public Object doAround(ProceedingJoinPoint joinPoint, TX tx) throws SQLException {
        log.info("环绕通知之开始");
        String serviceName = tx.serviceName();
        Object[] args = joinPoint.getArgs();
        final String groupId = (String) args[2];

        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        // 事物隔离级别，开启新事务，这样会比较安全些。
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
        // 获得事务状态
        TransactionStatus status = dataSourceTransactionManager.getTransaction(def);

        //创建exchange对象，用于线程之间通信
        Exchanger<String> exchanger = new Exchanger<String>();
        //全局变量中存放exchanger，让其它线程获取该exchanger
        TxClient.putExchanger(groupId, exchanger);
        try {
            Object proceed = joinPoint.proceed();
            if(proceed.equals(Boolean.TRUE)){
                //实际方法返回是true说明事务完成
                txManagerClient.createTxGroup(groupId,serviceName, TypeEnum.OK.getName());
            }else{
                //实际方法返回是false说明事务完成
                txManagerClient.createTxGroup(groupId,serviceName, TypeEnum.NO.getName());
            }
            return proceed;
        } catch (Throwable e) {
            log.error("修改数据库出现异常，回滚数据");
            e.printStackTrace();
            txManagerClient.createTxGroup(groupId,serviceName,TypeEnum.NO.getName());
            return false;
        }finally {
            //如果是发起事务组的，就调用tx-manager接口通知所有挂起事务的服务是提交事务还是回滚事务
            if(tx.initial()){
                final int count = tx.serviceCount();
                //异步调用让tx-manager去判断并通知所有服务
                executorService.execute(()-> txManagerClient.judgeTx(groupId,String.valueOf(count)));
            }
            log.info("环绕通知之结束");
            String receiveResult;
            try {
                //线程处于阻塞等待状态
                receiveResult = exchanger.exchange("GET", TimeOut.MAX_WAIT_EXCHANGE, TimeOut.MAX_WAIT_EXCHANGE_UNIT);
                log.info("服务从exchanger获取结果: {}", receiveResult);
                if(TypeEnum.OK.getName().equals(receiveResult)){
                    //成功则提交事务
                    dataSourceTransactionManager.commit(status);
                }else{
                    //失败则回滚事务
                    dataSourceTransactionManager.rollback(status);
                }
            } catch (InterruptedException | TimeoutException e) {
                e.printStackTrace();
                //出现异常则回滚事务
                dataSourceTransactionManager.rollback(status);
                //移除exchanger
                TxClient.removeExchanger(groupId);
            }
        }
    }

//    @AfterReturning(value = "serviceStatistics(tx)")
//    public void doAfterReturning(JoinPoint joinPoint, TX tx){
//        //如果是发起事务组的，就调用tx-manager接口通知所有挂起事务的服务是提交事务还是回滚事务
//        if(tx.initial()){
//            log.info("最后处理所有事务操作");
//            Object[] args = joinPoint.getArgs();
//            String groupId = (String) args[2];
//            txManagerClient.judgeTx(groupId);
//        }
//    }


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
