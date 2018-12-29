package com.fuzamei.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author ylx
 * Created by ylx on 2018/12/25.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface TX {

    /**
     * true表示是事务的发起人
     * @return
     */
    boolean initial() default false;

    /**
     * 当前的服务名
     * @return
     */
    String serviceName();

    /**
     * 参与的服务数量
     * @return
     */
    int serviceCount() default 0;

}
