package com.fuzamei.controller;

import com.fuzamei.enums.ResponseEnum;
import com.fuzamei.service.Aservice;
import com.fuzamei.txclient.TxClient;
import com.fuzamei.utils.UUIDUtils;
import com.sun.org.apache.bcel.internal.generic.RETURN;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;
import java.util.concurrent.Exchanger;

/**
 * @author ylx
 * Created by ylx on 2018/12/25.
 */
@RestController
@RequestMapping("/servicea")
public class Acontroller {

    private final Aservice aservice;

    @Autowired
    public Acontroller(Aservice aservice) {
        this.aservice = aservice;
    }

    @PostMapping("/update/{id}/{money}")
    public String distributeUpdate(@PathVariable(value = "id") String id,
                                   @PathVariable(value = "money") String money){
        try {
            Double moneys = Double.parseDouble(money);
            //由发起方生成一个uuid作为groupId
            String groupId = UUIDUtils.getUUID();
            //对a服务的数据库的数据进行修改
            boolean success = aservice.updateMoneya(id,moneys, groupId);
            return success ? ResponseEnum.SUCCESS.getName() : ResponseEnum.FAIL.getName();
        }catch (Exception e){
            e.printStackTrace();
            return ResponseEnum.FAIL.getName();
        }
    }


    @PostMapping("/exchange1/{id}")
    public String exchange1(@PathVariable(value = "id") String id) throws InterruptedException {
        Exchanger<String> exchanger = new Exchanger<>();
        TxClient.putExchanger(id,exchanger);
        String get = exchanger.exchange("GET");
        return get;
    }

    @PostMapping("/exchange2/{id}")
    public String exchange2(@PathVariable(value = "id") String id) throws InterruptedException {
        Exchanger<String> exchanger = TxClient.getExchanger(id);
        String send = exchanger.exchange("SEND");
        return send;
    }

}
