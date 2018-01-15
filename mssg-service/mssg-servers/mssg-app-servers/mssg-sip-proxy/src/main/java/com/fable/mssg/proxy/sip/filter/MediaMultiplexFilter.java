package com.fable.mssg.proxy.sip.filter;

import com.fable.framework.proxy.MessageFilter;
import com.fable.framework.proxy.sip.media.MediaService;
import com.fable.framework.proxy.sip.session.SipSessionManager;
import com.fable.framework.proxy.sip.util.SipUtils;
import io.netty.channel.ChannelHandlerContext;
import io.pkts.packet.sip.SipMessage;
import io.pkts.packet.sip.address.SipURI;
import io.sipstack.netty.codec.sip.SipMessageEvent;
import lombok.Setter;
import lombok.SneakyThrows;
import org.springframework.beans.BeansException;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import javax.sdp.MediaDescription;
import javax.sdp.SessionDescription;

public class MediaMultiplexFilter implements MessageFilter<SipMessageEvent>, ApplicationContextAware {

    private SipSessionManager sessionManager;

    @Setter
    private MediaService mediaService;

    @Setter
    private boolean isSslEnabled;

    @Setter
    private CacheManager cacheManager;

    private Cache dsCodeAndMediaportCache;

    private Cache dsCodeCountCache;

    @Override
    @SneakyThrows
    public boolean doFilter(ChannelHandlerContext ctx, SipMessageEvent event) {
        SipMessage message = event.getMessage();
        String callId = message.getCallIDHeader().getCallId().toString();
        boolean flag;
        int count;
        if (message.isInvite()) {
            SipURI toSipURI = (SipURI) message.getToHeader().getAddress().getURI();
            String dsCode = toSipURI.getUser().toString();
            count = dsCodeCountCache.get(dsCode) == null ? 0 : (int) dsCodeCountCache.get(dsCode).get();
            if (count == 0) {
                flag = true;
            } else {
                //todo 将callid、dscode、mediaport放入缓存。
                SessionDescription sessionDescription = (SessionDescription) message.getContent();
                String ip = sessionDescription.getConnection().getAddress();
                int port = SipUtils.getFirstMedia(sessionDescription).getMediaPort();
                int listenPort = (int) dsCodeAndMediaportCache.get(dsCode).get();
                mediaService.addMediaChannels(ip, port, listenPort);
                flag = false;
            }
        } else {
            flag = true;
        }
        return flag;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.sessionManager = applicationContext.getBean(SipSessionManager.class);
        this.dsCodeAndMediaportCache = cacheManager.getCache("dscodeandmediaport");
        this.dsCodeCountCache = cacheManager.getCache("count");
    }
}
