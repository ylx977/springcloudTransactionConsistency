package com.fuzamei.service;

import org.springframework.scheduling.annotation.Async;

/**
 * Created by ylx on 2018/12/27.
 */
public interface NotifyService {
    void distributeUpdateb(String id, String s, String groupId);

    void judgeTx(String groupId, Integer count);

    void distributeUpdatec(String id, String s, String groupId);
}
