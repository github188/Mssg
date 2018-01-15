package com.fable.mssg.proxy.media.listener;

import io.netty.channel.Channel;
import io.netty.util.AttributeKey;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import sun.misc.Cache;

import java.net.InetSocketAddress;

@Component
@Slf4j
public class RemoveCallidCache {

    @Autowired
    private CacheManager cacheManager;

    private static final String SIP_REQ_CACHE = "_sip_req";
    private static final String SIP_RESP_CACHE = "_sip_resp";

    @EventListener
    @Async
    @SneakyThrows
    public void removeCallidCache(Channel channel){
        String port = String.valueOf(((InetSocketAddress)channel.localAddress()).getPort());
        String callId = (String) channel.attr(AttributeKey.valueOf(port)).get();
        if(callId != null){
            cacheManager.getCache(SIP_REQ_CACHE).evict(callId);
            cacheManager.getCache(SIP_RESP_CACHE).evict(callId);
        }
    }
}
