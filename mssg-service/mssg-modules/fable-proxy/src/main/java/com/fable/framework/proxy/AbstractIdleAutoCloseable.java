package com.fable.framework.proxy;

import lombok.SneakyThrows;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * .
 *
 * @author stormning 2017/9/18
 * @since 1.3.0
 */
public abstract class AbstractIdleAutoCloseable implements IdleAutoCloseable {

    private static final ScheduledExecutorService EXECUTOR_SERVICE = Executors.newScheduledThreadPool(2);

    private long idleTimeMillis = 0;

    private long lastActiveTimeMillis = 0;

    private void initIdleTask() {
        EXECUTOR_SERVICE.schedule(new IdleTask(), idleTimeMillis, TimeUnit.MILLISECONDS);
    }

    @Override
    public void setIdleTime(long idleTime, TimeUnit timeUnit) {
        if (idleTime > 0) {
            this.idleTimeMillis = timeUnit.toMillis(idleTime);
            initIdleTask();
        }
    }

    @Override
    public void setLastActiveTime(long lastActiveTime, TimeUnit timeUnit) {
        this.lastActiveTimeMillis = timeUnit.toMillis(lastActiveTime);
    }

    private class IdleTask implements Runnable {
        @Override
        @SneakyThrows
        public void run() {
            long nextDelay = idleTimeMillis;
            nextDelay -= System.currentTimeMillis() - lastActiveTimeMillis;
            if (nextDelay <= 0) {
                destroy();
            } else {
                EXECUTOR_SERVICE.schedule(this, nextDelay, TimeUnit.MILLISECONDS);
            }
        }
    }
}
