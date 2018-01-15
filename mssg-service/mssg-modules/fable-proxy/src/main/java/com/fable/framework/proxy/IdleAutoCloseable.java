package com.fable.framework.proxy;

import java.util.concurrent.TimeUnit;

/**
 * .
 *
 * @author stormning 2017/9/18
 * @since 1.3.0
 */
public interface IdleAutoCloseable extends LifeCycle {

    void setIdleTime(long seconds, TimeUnit timeUnit);

    void setLastActiveTime(long lastActive, TimeUnit timeUnit);
}
