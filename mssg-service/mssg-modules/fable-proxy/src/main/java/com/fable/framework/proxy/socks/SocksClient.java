package com.fable.framework.proxy.socks;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.*;
import io.netty.handler.proxy.Socks5ProxyHandler;

import java.net.InetSocketAddress;

import static io.netty.handler.codec.http.HttpMethod.GET;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;
import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * .
 *
 * @author stormning 2017/12/4
 * @since 1.3.0
 */
public class SocksClient {
    public static void main(String[] args) {
        final String ua = "Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2228.0 Safari/537.36";
        final String host = "www.baidu.com";
        final int port = 80;

        Bootstrap b = new Bootstrap();
        b.group(new NioEventLoopGroup())
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ChannelPipeline p = ch.pipeline();

                        p.addLast(new HttpClientCodec());
                        p.addLast(new HttpContentDecompressor());
                        p.addLast(new HttpObjectAggregator(10_485_760));
                        p.addLast(new ChannelInboundHandlerAdapter() {
                            @Override
                            public void channelActive(final ChannelHandlerContext ctx) throws Exception {
                                HttpRequest request = new DefaultFullHttpRequest(HTTP_1_1, GET, "/");
                                request.headers().set("HOST", host + ":" + port);
                                request.headers().set("USER_AGENT", ua);
                                request.headers().set("CONNECTION", "CLOSE");

                                ctx.writeAndFlush(request);

                                System.out.println("!sent");
                            }

                            @Override
                            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                System.out.println("!answer");
                                if (msg instanceof FullHttpResponse) {
                                    FullHttpResponse httpResp = (FullHttpResponse) msg;


                                    ByteBuf content = httpResp.content();
                                    String strContent = content.toString(UTF_8);
                                    System.out.println("body: " + strContent);

                                    return;
                                }

                                super.channelRead(ctx, msg);
                            }

                            @Override
                            public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
                                cause.printStackTrace(System.err);
                                ctx.close();
                            }
                        });

                        p.addFirst(new Socks5ProxyHandler(new InetSocketAddress("192.168.30.60", 1086)));
                    }
                });

        b.connect(host, port).awaitUninterruptibly();
    }
}
