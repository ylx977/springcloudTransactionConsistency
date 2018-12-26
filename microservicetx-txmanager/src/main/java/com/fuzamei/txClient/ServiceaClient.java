package com.fuzamei.txClient;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.cloud.netflix.feign.FeignClientsConfiguration;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

/**
 * Created by ylx on 2018/12/26.
 */
@FeignClient(name = "SERVERA",configuration = FeignClientsConfiguration.class)
public interface ServiceaClient {

    @PostMapping("/txa/decide/{txId}/{flag}")
    String decideConnection(@PathVariable(value = "txId") String txId,
                            @PathVariable(value = "flag") String flag);

}
