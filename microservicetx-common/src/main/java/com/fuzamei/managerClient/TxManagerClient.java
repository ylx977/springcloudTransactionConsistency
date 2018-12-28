package com.fuzamei.managerClient;

import com.fuzamei.constants.ServiceName;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.cloud.netflix.feign.FeignClientsConfiguration;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

/**
 * @author ylx
 * Created by ylx on 2018/12/26.
 */
@FeignClient(name = ServiceName.TX_MANAGER,configuration = FeignClientsConfiguration.class)
public interface TxManagerClient {

    /**
     * 创建事务组或加入事务组
     * @param groupId
     * @param serviceName
     * @param type
     * @return
     */
    @PostMapping("/txmanage/createTxGroup/{groupId}/{serviceName}/{type}")
    String createTxGroup(@PathVariable(value = "groupId") String groupId,
                         @PathVariable(value = "serviceName") String serviceName,
                         @PathVariable(value = "type") String type);

    @PostMapping("/txmanage/judgeTx/{groupId}/{count}")
    String judgeTx(@PathVariable(value = "groupId") String groupId,
                   @PathVariable(value = "count") String count);


}
