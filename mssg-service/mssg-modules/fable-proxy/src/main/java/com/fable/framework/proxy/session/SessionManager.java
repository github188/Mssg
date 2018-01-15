package com.fable.framework.proxy.session;

/**
 * lifecycle  new --> save.
 *
 * @author stormning 2017/11/9
 * @since 1.3.0
 */
public interface SessionManager<S extends Session<T>, T, O> {

    S getSession(String sessionId);

    S newSession(O message);

    S save(S sipSession);
}
