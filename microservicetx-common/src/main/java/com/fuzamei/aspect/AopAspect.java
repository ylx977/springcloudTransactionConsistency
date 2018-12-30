package com.fuzamei.aspect;

import com.fuzamei.annotations.TX;
import com.fuzamei.enums.ResponseEnum;
import com.fuzamei.enums.TypeEnum;
import com.fuzamei.managerClient.TxManagerClient;
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
import java.util.concurrent.ExecutorService;

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
        int count = tx.serviceCount();
        Object[] args = joinPoint.getArgs();
        final String groupId = (String) args[2];

        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        // 事物隔离级别，开启新事务，这样会比较安全些。
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
        // 获得事务状态
        TransactionStatus status = dataSourceTransactionManager.getTransaction(def);

        try {
            Object proceed = joinPoint.proceed();
            if(proceed.equals(Boolean.TRUE)){
                //实际方法返回是true说明事务完成
                txManagerClient.createTxGroup(groupId,serviceName, TypeEnum.OK.getName());
            }else{
                //实际方法返回是false说明事务完成
                txManagerClient.createTxGroup(groupId,serviceName, TypeEnum.NO.getName());
            }

            //同步等待返回结果
            String result = null;
            try {
                result = txManagerClient.judgeTx(groupId, String.valueOf(count));
            }catch (Exception e){
                //调用tx-manager服务失败也回滚数据
                log.error("调用tx-manager失败回滚数据");
                dataSourceTransactionManager.rollback(status);
            }
            if(ResponseEnum.SUCCESS.getName().equals(result)){
                dataSourceTransactionManager.commit(status);
            }else{
                dataSourceTransactionManager.rollback(status);
            }

            return proceed;
        } catch (Throwable e) {
            log.error("修改数据库出现异常，回滚数据");
            e.printStackTrace();
            //向tx-manager发送本服务事务提交失败的通知
            txManagerClient.createTxGroup(groupId,serviceName,TypeEnum.NO.getName());

            //不需要从tx-manager调接口，直接自己回滚即可
            //失败则回滚事务
            dataSourceTransactionManager.rollback(status);
            return false;
        }finally {
            log.info("环绕通知之结束");
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
