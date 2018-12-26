package com.fuzamei.controller;

import com.fuzamei.service.Aservice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

/**
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
        Double moneys = Double.parseDouble(money);
        String groupId = UUID.randomUUID().toString().replaceAll("-", "");
        boolean success = aservice.updateMoneya(id,moneys, groupId);
        return success ? "success" : "fail";
    }

}
