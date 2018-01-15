package com.fable.framework.proxy.sip.session;

/**
 * TCP连接方式.
 * A passive open is the creation of a listening socket, to accept incoming connections.
 * It uses socket(), bind(), listen(), followed by an accept() loop.
 * <p>
 * An active open is the creation of a connection to a listening port by a input.
 * It uses socket() and connect().
 *
 * @author stormning 2017/11/9
 * @since 1.3.0
 */
public enum Setup {
    active, passive
}
