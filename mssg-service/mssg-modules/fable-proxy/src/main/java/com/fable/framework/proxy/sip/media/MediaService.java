package com.fable.framework.proxy.sip.media;

import com.fable.framework.proxy.sip.Address;
import com.fable.framework.proxy.sip.session.SipSessionKeeper;

/**
 * .
 *
 * @author stormning 2017/11/9
 * @since 1.3.0
 */
public interface MediaService {

    Address acquire(boolean request);

    void createMediaChannels(SipSessionKeeper session);

    void addMediaChannels(String ip, int port, int listenPort);
}
