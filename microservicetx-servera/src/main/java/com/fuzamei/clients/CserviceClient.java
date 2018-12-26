package com.fuzamei.clients;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.cloud.netflix.feign.FeignClientsConfiguration;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

/**
 * Created by ylx on 2018/12/25.
 */
@FeignClient(name = "SERVERC",configuration = FeignClientsConfiguration.class)
public interface CserviceClient {

    @PostMapping("/servicec/update/{id}/{money}/{groupId}")
    String distributeUpdate(@PathVariable(value = "id") String id,
                            @PathVariable(value = "money") String money,
                            @PathVariable(value = "groupId") String groupId);

}
