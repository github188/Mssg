package com.fable.mssg.proxy.sip.codec;

import com.fable.mssg.service.equipment.SecurityService;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.DefaultAddressedEnvelope;
import io.netty.channel.socket.DatagramPacket;
import io.netty.handler.codec.MessageToMessageEncoder;
import lombok.Setter;

import java.net.InetSocketAddress;
import java.util.List;

public class EncryptEncoder extends MessageToMessageEncoder<Object> {
    @Setter
    private SecurityService securityService;

    @Override
    protected void encode(ChannelHandlerContext ctx, Object msg, List<Object> out) throws Exception {
        //信令加密，有数据库操作，待优化。
        DefaultAddressedEnvelope defaultAddressedEnvelope = null;
        if(msg instanceof DefaultAddressedEnvelope){
            defaultAddressedEnvelope = (DefaultAddressedEnvelope) msg;
        }
        byte[] b = securityService.isEncryption(defaultAddressedEnvelope, "0002");
        DatagramPacket datagramPacket = new DatagramPacket(Unpooled.copiedBuffer(b), (InetSocketAddress) defaultAddressedEnvelope.recipient());
        out.add(datagramPacket);
    }

}
