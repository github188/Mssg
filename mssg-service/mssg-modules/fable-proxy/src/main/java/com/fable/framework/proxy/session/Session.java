package com.fable.framework.proxy.session;

/**
 * .
 *
 * @author stormning 2017/11/8
 * @since 1.3.0
 */
public interface Session<L> {

    String getId();

    L getRaw();

    void setAttribute(Object key, Object value);

    Object getAttribute(Object key);
}
