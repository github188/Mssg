package com.fable.mssg.proxy.sip.codec;

import com.fable.mssg.service.equipment.SecurityService;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.DatagramPacket;
import io.netty.handler.codec.MessageToMessageDecoder;
import lombok.Setter;

import java.util.List;

public class DecipheringDecoder extends MessageToMessageDecoder<DatagramPacket> {
    @Setter
    private SecurityService securityService;

    @Override
    protected void decode(ChannelHandlerContext ctx, DatagramPacket msg, List<Object> out) throws Exception {
        //信令解密，有数据库操作，待优化。
        byte[] b = securityService.isDecode(msg, "0002");
        DatagramPacket datagramPacket = new DatagramPacket(Unpooled.copiedBuffer(b), msg.recipient(), msg.sender());
        out.add(datagramPacket);
    }
}
