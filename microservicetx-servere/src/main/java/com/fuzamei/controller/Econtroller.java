package com.fuzamei.controller;

import com.fuzamei.enums.ResponseEnum;
import com.fuzamei.service.Eservice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by ylx on 2018/12/25.
 */
@RestController
@RequestMapping("/servicee")
public class Econtroller {

    private final Eservice eservice;

    @Autowired
    public Econtroller(Eservice eservice) {
        this.eservice = eservice;
    }

    @PostMapping("/update/{id}/{money}/{groupId}")
    public String distributeUpdate(@PathVariable(value = "id") String id,
                                   @PathVariable(value = "money") String money,
                                   @PathVariable(value = "groupId") String groupId){
        Double moneys = Double.parseDouble(money);
        try {
            boolean success = eservice.updateMoneye(id,moneys,groupId);
            return success ? ResponseEnum.SUCCESS.getName() : ResponseEnum.FAIL.getName();
        }catch (Exception e){
            e.printStackTrace();
            return ResponseEnum.FAIL.getName();
        }
    }

}
