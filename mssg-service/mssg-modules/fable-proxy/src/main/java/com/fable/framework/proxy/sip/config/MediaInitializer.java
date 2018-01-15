package com.fable.framework.proxy.sip.config;

import com.fable.framework.proxy.sip.media.MediaServiceImpl;

/**
 * .
 *
 * @author stormning 2017/11/27
 * @since 1.3.0
 */
public interface MediaInitializer {
    void customize(MediaServiceImpl mediaService);
}
