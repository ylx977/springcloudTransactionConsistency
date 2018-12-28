package com.fuzamei.txclient;

import java.sql.Connection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Exchanger;

/**
 * @author ylx
 * @mail yanglingxiao123@qq.com
 * Created by ylx on 2018/12/25.
 */
public class TxClient {

    /**
     * 存放连接的map
     */
    private static volatile ConcurrentHashMap<String,Connection> connectionMap = new ConcurrentHashMap<String, Connection>();

    /**
     * 存放exchanger的map
     */
    private static volatile ConcurrentHashMap<String,Exchanger<String>> exchangerMap = new ConcurrentHashMap<String,Exchanger<String>>();


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

    /**
     * 获取exchanger
     * @param txId
     * @return
     */
    public static Exchanger<String> getExchanger(String txId){
        Exchanger<String> exchanger = exchangerMap.get(txId);
        if(exchanger == null){
            return null;
        }
        //将连接从map中移除
        exchangerMap.remove(txId);
        return exchanger;
    }

    /**
     * 将exchanger放入map
     * @param txId
     * @param exchanger
     * @return
     */
    public static void putExchanger(String txId,Exchanger<String> exchanger){
        if(exchanger == null){
            return;
        }
        Exchanger<String> put = exchangerMap.put(txId, exchanger);
    }
}
