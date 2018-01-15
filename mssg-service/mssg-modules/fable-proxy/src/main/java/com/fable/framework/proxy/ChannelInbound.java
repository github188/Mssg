package com.fable.framework.proxy;

import io.netty.channel.ChannelHandlerContext;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * .
 *
 * @author stormning 2017/11/6
 * @since 1.3.0
 */
@Data
@AllArgsConstructor
public class ChannelInbound {

    /**
     * input channel
     */
    private ChannelHandlerContext context;


    /**
     * input bootstrap listener
     */
    private ClientListener clientClientListener;
}
