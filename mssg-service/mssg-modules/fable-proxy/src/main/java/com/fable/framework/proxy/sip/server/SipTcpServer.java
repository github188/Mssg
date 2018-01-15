package com.fable.framework.proxy.sip.server;

import com.fable.framework.proxy.ChannelManager;
import com.fable.framework.proxy.DefaultTcpServer;

/**
 * .
 *
 * @author stormning 2017/10/31
 * @since 1.3.0
 */
//TODO
public class SipTcpServer extends DefaultTcpServer {
    public SipTcpServer(ChannelManager channelManager) {
        super(channelManager);
    }

   /* private InetSocketAddress clientAddress;

    private InetSocketAddress remoteAddress;

    @Setter
    private List<SipMessageHandler<SipRequest>> sipRequestHandlers;

    @Setter
    private List<SipMessageHandler<SipResponse>> sipResponseHandlers;

    @Setter
    private SipSessionManager sessionManager;
    //client handler
    //handler response and send back to server channel
    private final ClientListener DEFAULT_CLIENT_BOOTSTRAP_LISTENER = (contextIn, bootstrap) ->
            bootstrap.handler(new ChannelInitializer<Channel>() {
                @Override
                protected void initChannel(Channel ch) throws Exception {
                    ChannelPipeline pipeline = ch.pipeline();
                    pipeline.addLast(ExceptionHandler.INSTANCE);
                    pipeline.addLast(new SipMessageStreamDecoder());
                    pipeline.addLast(new SipMessageEncoder());
                    pipeline.addLast(getSipResponseSipProxyHandlerAdapter(contextIn));
                }
            });

    public SipTcpServer(ChannelManager channelManager, InetSocketAddress clientAddress, InetSocketAddress remoteAddress) {
        super(channelManager);
        this.clientAddress = clientAddress;
        this.remoteAddress = remoteAddress;
    }

    @Override
    protected ServerBootstrap createBootstrap(EventLoopGroup workerGroup) {
        return super.createBootstrap(workerGroup)*//*.childOption(ChannelOption.AUTO_READ, false)*//*;
    }

    @Override
    protected void customizeChannelPipeline(ChannelPipeline pipeline) {
        super.customizeChannelPipeline(pipeline);
        pipeline.addLast(new SipMessageStreamDecoder());
        pipeline.addLast(new SipMessageEncoder());
        //TODO use socks proxy
        pipeline.addLast(getSipRequestSipProxyHandlerAdapter());
    }

    private SipProxyHandlerAdapter<SipRequest> getSipRequestSipProxyHandlerAdapter() {
        SipProxyHandlerAdapter<SipRequest> requestHandlerAdapter = new SipProxyHandlerAdapter<>();
        requestHandlerAdapter.setClientListener(DEFAULT_CLIENT_BOOTSTRAP_LISTENER);
        requestHandlerAdapter.setClientAddress(clientAddress);
        requestHandlerAdapter.setRemoteAddress(remoteAddress);
        requestHandlerAdapter.setSipMessageHandlers(sipRequestHandlers);
        requestHandlerAdapter.setChannelManager(getChannelManager());
        //request handler
        DefaultSipRequestHandler sipRequestHandler = new DefaultSipRequestHandler(clientAddress, remoteAddress);
        sipRequestHandler.setSessionManager(sessionManager);
        requestHandlerAdapter.setDefaultSipMessageHandler(sipRequestHandler);
        return requestHandlerAdapter;
    }

    private SipProxyHandlerAdapter<SipResponse> getSipResponseSipProxyHandlerAdapter(ChannelHandlerContext contextIn) {
        SipProxyHandlerAdapter<SipResponse> responseHandlerAdapter = new SipProxyHandlerAdapter<>();
        responseHandlerAdapter.setSipMessageHandlers(sipResponseHandlers);
        responseHandlerAdapter.setChannelManager(getChannelManager());
        InetSocketAddress clientAddress = (InetSocketAddress) contextIn.channel().localAddress();
        responseHandlerAdapter.setClientAddress(clientAddress);

        //response handler
        DefaultSipResponseHandler sipResponseHandler = new DefaultSipResponseHandler(clientAddress, null);
        sipResponseHandler.setSessionManager(sessionManager);
        responseHandlerAdapter.setDefaultSipMessageHandler(sipResponseHandler);
        return responseHandlerAdapter;
    }*/
}