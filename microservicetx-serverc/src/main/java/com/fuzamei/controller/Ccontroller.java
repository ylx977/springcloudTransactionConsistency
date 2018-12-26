package com.fuzamei.controller;

import com.fuzamei.service.Cservice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by ylx on 2018/12/25.
 */
@RestController
@RequestMapping("/servicec")
public class Ccontroller {

    private final Cservice cservice;

    @Autowired
    public Ccontroller(Cservice cservice) {
        this.cservice = cservice;
    }

    @PostMapping("/update/{id}/{money}/{groupId}")
    public String distributeUpdate(@PathVariable(value = "id") String id,
                                   @PathVariable(value = "money") String money,
                                   @PathVariable(value = "groupId") String groupId){
        Double moneys = Double.parseDouble(money);
        boolean success = cservice.updateMoneyb(id,moneys,groupId);
        return success ? "success" : "fail";
    }

}
