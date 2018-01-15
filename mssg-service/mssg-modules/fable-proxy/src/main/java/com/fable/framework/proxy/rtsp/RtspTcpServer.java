package com.fable.framework.proxy.rtsp;

import com.fable.framework.proxy.ChannelManager;
import com.fable.framework.proxy.DefaultTcpServer;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.rtsp.RtspDecoder;
import io.netty.handler.codec.rtsp.RtspEncoder;

/**
 * TODO.
 * For gbt28181-2016 媒体回放控制命令
 *
 * @author stormning 2017/10/23
 * @since 1.3.0
 */
public class RtspTcpServer extends DefaultTcpServer {

    public RtspTcpServer(ChannelManager channelManager) {
        super(channelManager);
    }

    @Override
    protected void customizeChannelPipeline(ChannelPipeline pipeline) {
        super.customizeChannelPipeline(pipeline);
        pipeline.addLast(new RtspDecoder());
        pipeline.addLast(new RtspEncoder());
        pipeline.addLast(new HttpObjectAggregator(1048576));
        pipeline.addLast(new RtspRequestHandler());
    }
}
