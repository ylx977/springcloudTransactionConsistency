package com.fuzamei.managerClient;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.cloud.netflix.feign.FeignClientsConfiguration;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

/**
 * Created by ylx on 2018/12/26.
 */
@FeignClient(name = "TX-MANAGER",configuration = FeignClientsConfiguration.class)
public interface TxManagerClient {

    @PostMapping("/txmanage/createTxGroup/{groupId}/{serviceName}/{type}")
    String createTxGroup(@PathVariable(value = "groupId") String groupId,
                         @PathVariable(value = "serviceName") String serviceName,
                         @PathVariable(value = "type") String type);

    @PostMapping("/txmanage/judgeTx/{groupId}")
    String judgeTx(@PathVariable(value = "groupId") String groupId);


}
