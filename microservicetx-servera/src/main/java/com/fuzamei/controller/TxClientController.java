package com.fuzamei.controller;

import com.fuzamei.constants.TimeOut;
import com.fuzamei.enums.ResponseEnum;
import com.fuzamei.enums.TypeEnum;
import com.fuzamei.txclient.TxClient;
import com.netflix.discovery.converters.Auto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.Exchanger;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * @author ylx
 * Created by ylx on 2018/12/26.
 */
@Slf4j
@RestController
@RequestMapping("/txa")
public class TxClientController {

    /**
     * flag如果是OK表示成功，提交事务
     * @param txId
     * @param flag
     * @return
     * @throws SQLException
     */
    @PostMapping("/decide/{txId}/{flag}")
    public String decideConnection(@PathVariable(value = "txId") String txId,
                                   @PathVariable(value = "flag") String flag) {
        log.info("A服务被TX-MANAGER通知");
        Exchanger<String> exchanger = TxClient.getExchanger(txId);
        if(exchanger == null){
            return ResponseEnum.FAIL.getName();
        }
        try {
            if(TypeEnum.OK.getName().equals(flag)){
                log.info("A服务准备将事务提交上去");
                //通知挂起的线程消息
                exchanger.exchange(flag, TimeOut.MAX_WAIT_EXCHANGE, TimeOut.MAX_WAIT_EXCHANGE_UNIT);
            }else if(TypeEnum.NO.getName().equals(flag)){
                log.info("A服务准备将事务回滚");
                exchanger.exchange(flag, TimeOut.MAX_WAIT_EXCHANGE, TimeOut.MAX_WAIT_EXCHANGE_UNIT);
            }else{
                throw new RuntimeException("flag类型错误");
            }
        }catch (InterruptedException | TimeoutException e){
            e.printStackTrace();
            //出异常删除exchangeMap中的exchanger
            TxClient.removeExchanger(txId);
            return ResponseEnum.FAIL.getName();
        }
        return ResponseEnum.SUCCESS.getName();
    }

}
