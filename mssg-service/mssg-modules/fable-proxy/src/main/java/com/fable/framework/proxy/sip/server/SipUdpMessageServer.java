package com.fable.framework.proxy.sip.server;

import com.fable.framework.proxy.ChannelManager;
import com.fable.framework.proxy.DefaultUdpServer;
import com.fable.framework.proxy.decode.SipMessageDecoder;
import com.fable.framework.proxy.sip.handler.SipMessageHandler;
import com.fable.framework.proxy.sip.handler.SipProxyHandlerAdapter;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelPipeline;
import io.pkts.packet.sip.SipMessage;
import io.sipstack.netty.codec.sip.SipMessageEncoder;
import lombok.Getter;
import lombok.Setter;

/**
 * .
 *
 * @author stormning 2017/11/14
 * @since 1.3.0
 */
public class SipUdpMessageServer extends DefaultUdpServer {

    private ChannelHandler[] customHandlers;

    @Setter
    @Getter
    private SipProxyHandlerAdapter proxyHandlerAdapter;


    public SipUdpMessageServer(ChannelManager channelManager) {
        super(channelManager);
    }

    @Override
    protected void customizeChannelPipeline(ChannelPipeline pipeline) {
        super.customizeChannelPipeline(pipeline);
        if (customHandlers != null) {
            for (ChannelHandler serverDecoder : customHandlers) {
                pipeline.addLast(serverDecoder);
            }
        }
        pipeline.addLast(new SipMessageDecoder());
        pipeline.addLast(new SipMessageEncoder());
        pipeline.addLast(proxyHandlerAdapter);
    }

    public void setCustomHandlers(ChannelHandler... decoders) {
        this.customHandlers = decoders;
    }
}
