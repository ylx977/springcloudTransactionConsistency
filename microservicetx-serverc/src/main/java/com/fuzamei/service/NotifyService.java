package com.fuzamei.service;

/**
 * Created by ylx on 2018/12/27.
 */
public interface NotifyService {
    void distributeUpdated(String id, String s, String groupId);

    void distributeUpdatee(String id, String s, String groupId);
}
