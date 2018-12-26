package com.fuzamei.controller;

import com.fuzamei.service.Bservice;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by ylx on 2018/12/25.
 */
@Slf4j
@RestController
@RequestMapping("/serviceb")
public class Bcontroller {

    private final Bservice bservice;

    @Autowired
    public Bcontroller(Bservice bservice) {
        this.bservice = bservice;
    }

    @PostMapping("/update/{id}/{money}/{groupId}")
    public String distributeUpdate(@PathVariable(value = "id") String id,
                                   @PathVariable(value = "money") String money,
                                   @PathVariable(value = "groupId") String groupId){
        Double moneys = Double.parseDouble(money);
        try {
            boolean success = bservice.updateMoneyb(id,moneys,groupId);
            log.info(success+"");
            return success ? "success" : "fail";
        }catch (Exception e){
            e.printStackTrace();
            return "fail";
        }
    }

}
