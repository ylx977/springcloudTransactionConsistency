package com.fuzamei.txclient;

import java.sql.Connection;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by ylx on 2018/12/25.
 */
public class TxClient {

    private static volatile ConcurrentHashMap<String,Connection> connectionMap = new ConcurrentHashMap<String, Connection>();


    /**
     * 获取连接
     * @param txId
     * @return
     */
    public static Connection getConnection(String txId){
        Connection connection = connectionMap.get(txId);
        if(connection == null){
            return null;
        }
        //将连接从map中移除
        connectionMap.remove(txId);
        return connection;
    }

    /**
     * 将连接放入map
     * @param txId
     * @param connection
     * @return
     */
    public static void putConnection(String txId,Connection connection){
        if(connection == null){
            return;
        }
        Connection put = connectionMap.put(txId, connection);
    }
}
