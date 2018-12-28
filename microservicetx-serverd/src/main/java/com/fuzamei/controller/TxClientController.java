package com.fuzamei.controller;

import com.fuzamei.enums.ResponseEnum;
import com.fuzamei.enums.TypeEnum;
import com.fuzamei.txclient.TxClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLException;
import java.util.concurrent.Exchanger;

/**
 * @author ylx
 * Created by ylx on 2018/12/26.
 */
@Slf4j
@RestController
@RequestMapping("/txd")
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
        Exchanger<String> exchanger = TxClient.getExchanger(txId);
        if(exchanger == null){
            return ResponseEnum.FAIL.getName();
        }
        try {
            if(TypeEnum.OK.getName().equals(flag)){
                log.info("D服务准备将事务提交上去");
                //通知挂起的线程消息
                exchanger.exchange(flag);
            }else if(TypeEnum.NO.getName().equals(flag)){
                log.info("D服务准备将事务回滚");
                exchanger.exchange(flag);
            }else{
                throw new RuntimeException("flag类型错误");
            }
        }catch (Exception e){
            e.printStackTrace();
            return ResponseEnum.FAIL.getName();
        }
        return ResponseEnum.SUCCESS.getName();
    }

}
