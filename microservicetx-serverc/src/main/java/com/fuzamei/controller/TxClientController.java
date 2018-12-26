package com.fuzamei.controller;

import com.fuzamei.txclient.TxClient;
import com.netflix.discovery.converters.Auto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * Created by ylx on 2018/12/26.
 */
@Slf4j
@RestController
@RequestMapping("/txc")
public class TxClientController {

    @Auto
    private DataSource dataSource;

    /**
     * flag如果是OK表示成功，提交事务
     * @param txId
     * @param flag
     * @return
     * @throws SQLException
     */
    @PostMapping("/decide/{txId}/{flag}")
    public String decideConnection(@PathVariable(value = "txId") String txId,
                                   @PathVariable(value = "flag") String flag) throws SQLException {
        Connection connection = TxClient.getConnection(txId);
        if(connection == null){
            return "fail";
        }
        try {
            if("OK".equals(flag)){
                log.info("C服务准备将事务提交上去");
                connection.commit();
            }else{
                log.info("C服务准备将事务回滚");
                connection.rollback();
            }
        }catch (Exception e){
            connection.rollback();
            return "fail";
        }finally {
            log.info("C服务最后将连接还回给数据库");
            DataSourceUtils.doCloseConnection(connection,dataSource);
        }
        return "success";
    }

}
