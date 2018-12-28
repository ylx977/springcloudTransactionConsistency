package com.fuzamei.threadpool;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.*;

/**
 * @author ylx
 * Created by fuzamei on 2018/4/25.
 */
@Configuration
public class ThreadPoolConfig {

    @Bean
    public ExecutorService createThreadPool() {
        MyRejectExecutionHandler handler = new MyRejectExecutionHandler();
        ThreadFactory threadFactory = Executors.defaultThreadFactory();
        ThreadPoolExecutor es = new ThreadPoolExecutor(16, 64, 60, TimeUnit.SECONDS, new LinkedBlockingQueue<>(256), threadFactory, handler) {

            private ThreadLocal<Long> threadLocal = new ThreadLocal<>();

            @Override
            protected void beforeExecute(Thread t, Runnable r) {//线程执行前的操作
                threadLocal.set(System.currentTimeMillis());
                System.out.println(t.getId() + "ThreadPool initiate.....");
            }

            @Override
            protected void afterExecute(Runnable r, Throwable t) {//线程执行后的操作
                Long startTime = threadLocal.get();
                threadLocal.remove();
                Long endTime = System.currentTimeMillis();
                System.out.println(Thread.currentThread().getId() + "thread finished...,耗时:" + (endTime - startTime) + "ms");
            }

            @Override
            protected void terminated() {
                this.shutdown();
                System.out.println("ThreadPool destroyed.....");
            }

        };
        es.prestartAllCoreThreads();
        return es;
    }

    public static class MyRejectExecutionHandler implements RejectedExecutionHandler {

        @Override
        public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
            throw new RuntimeException("线程池拒绝服务");
        }
    }

}
