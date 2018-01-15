package com.fable.framework.proxy.sip.handler;

import com.fable.framework.proxy.AbstractProxyHandler;
import com.fable.framework.proxy.ChannelManager;
import com.fable.framework.proxy.RouterInfo;
import com.fable.framework.proxy.session.Session;
import com.fable.framework.proxy.sip.session.*;
import com.fable.framework.proxy.sip.util.SipUtils;
import com.fable.framework.proxy.sip.util.SnapshotUtils;
import com.google.common.collect.Lists;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.pkts.packet.sip.SipMessage;
import io.sipstack.netty.codec.sip.SipMessageEvent;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;

import java.net.InetSocketAddress;
import java.util.List;

/**
 * .
 *
 * @author stormning 2017/11/6
 * @since 1.3.0
 */
@Slf4j
@ChannelHandler.Sharable
public class SipProxyHandlerAdapter
        extends AbstractProxyHandler<RouterInfo<SipMessage>, SipMessageEvent, ByteBuf> implements InitializingBean, ApplicationEventPublisherAware {

    @Setter
    private InetSocketAddress contactAddress;

    @Setter
    private List<SipMessageHandler<SipMessage>> sipMessageHandlers = Lists.newArrayList();

    private SipMessageHandler<SipMessageWrapper> defaultSipMessageHandler;

    private SipSessionManager sessionManager;

    private ApplicationEventPublisher eventPublisher;

    public SipProxyHandlerAdapter(ChannelManager channelManager, SipSessionManager sessionManager) {
        super(channelManager);
        this.sessionManager = sessionManager;
    }

    @Override
    @SuppressWarnings("unchecked")
    protected RouterInfo<SipMessage> handle(ChannelHandlerContext context, SipMessageEvent sipMessageEvent) {
        SipMessage message = sipMessageEvent.getMessage();
        log.info("Receive sip message , channel {}  message {}", context.channel(), message);
        //use default handler
        SipSession session = sessionManager.getSession(SnapshotUtils.getCallId(message));
        if (session == null) {
            //find support handler
            for (SipMessageHandler<SipMessage> handler : sipMessageHandlers) {
                if (handler.supports(message)) {
                    log.info("Message handler is {}", handler);
                    return handler.handle(message);
                }
            }
        }
        log.info("Use default handler");
        SipRouterInfo handled = defaultSipMessageHandler.handle(new SipMessageWrapper(message, session));
        log.info("Router info {} ", handled);
        return handled;
    }

    @Override
    protected ByteBuf getOutboundMessage(RouterInfo<SipMessage> handled) {
        return Unpooled.wrappedBuffer(SipUtils.encode(handled.getMessage()));
    }

    @Override
    protected InetSocketAddress getRemoteAddress(ChannelHandlerContext ctx, RouterInfo<SipMessage> handled) {
        InetSocketAddress handledRemoteAddress = handled.getRemoteAddress();
        return handledRemoteAddress == null ? super.getRemoteAddress(ctx, handled) : handledRemoteAddress;
    }

    @Override
    protected InetSocketAddress getLocalAddress(ChannelHandlerContext ctx, RouterInfo<SipMessage> handled) {
        InetSocketAddress addressFormMessage = handled.getLocalAddress();
        return addressFormMessage == null ? super.getLocalAddress(ctx, handled) : addressFormMessage;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        InternalSipMessageHandlerComposite composite = new InternalSipMessageHandlerComposite();
        composite.setResponseHandler(new DefaultSipResponseHandler(getClientAddress(), getRemoteAddress(), contactAddress, sessionManager));
        composite.setRequestHandler(new DefaultSipRequestHandler(getClientAddress(), getRemoteAddress(), contactAddress, sessionManager, getBackServerAddress()));
        DefaultSipMessageHandler defaultHandler = new DefaultSipMessageHandler(composite);
        defaultHandler.setApplicationEventPublisher(eventPublisher);
        this.defaultSipMessageHandler = defaultHandler;
    }

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }
}
