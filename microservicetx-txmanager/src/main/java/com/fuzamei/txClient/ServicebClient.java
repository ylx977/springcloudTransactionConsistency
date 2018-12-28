package com.fuzamei.txClient;

import com.fuzamei.constants.ServiceName;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.cloud.netflix.feign.FeignClientsConfiguration;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

/**
 * @author ylx
 * Created by ylx on 2018/12/26.
 */
@FeignClient(name = ServiceName.SERVICE_B,configuration = FeignClientsConfiguration.class)
public interface ServicebClient {

    /**
     * 向服务B发送事务到底是提交还是回滚
     * @param txId
     * @param flag
     * @return
     */
    @PostMapping("/txb/decide/{txId}/{flag}")
    String decideConnection(@PathVariable(value = "txId") String txId,
                            @PathVariable(value = "flag") String flag);

}
