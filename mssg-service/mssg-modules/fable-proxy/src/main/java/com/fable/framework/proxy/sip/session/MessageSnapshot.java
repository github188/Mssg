package com.fable.framework.proxy.sip.session;

import com.fable.framework.proxy.sip.Address;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.net.InetSocketAddress;

/**
 * 请求关键字段，for response回路.
 *
 * @author stormning 2017/11/18
 * @since 1.3.0
 */
public class MessageSnapshot implements Serializable {

    //Call-ID:wlss-e680b2c1-730beb6350@172.18.16.5

    /**
     * callId
     */

    @Setter
    @Getter
    private MappedProp<String> callId;


    /**
     * 媒体发送者设备编码
     */
    @Setter
    @Getter
    private MappedProp<String> toDeviceId;

    /**
     * 目的域名
     */
    @Setter
    @Getter
    private MappedProp<String> toHost;

    /**
     * 目的端口
     */
    @Setter
    @Getter
    private MappedProp<Integer> toPort;


    //via: SIP/2.0/UDP 源域名或ip地址
    //Via头域是被服务器插入request中，用来检查路由环的，并且可以使response根据via找到返回的路.
    //它不会对未来的request 或者是response造成影响。

    /**
     * 经由：源域名或IP地址
     */
    @Setter
    @Getter
    private MappedProp<String> viaHost;

    @Setter
    @Getter
    private MappedProp<Integer> viaPort;


    //From:<sip:源设备编码@源域名>;tag=BK32B1U8DKDrB
    //即上一个contact
    //如果一个SIP消息中没有Contact或者Record-Route头域，那么callee就会根据From头域产生后续的Request
    //比如：如果 Alice打一个电话给Bob，From头域的内容是 From:Alice<sip:alice@example.org>
    //那么Bob打给Alice时就会使用 sip:alice@example.org作为To头域和Request-URI头域的内容

    /**
     * 源设备编码
     */
    @Setter
    @Getter
    private MappedProp<String> fromDeviceId;

    /**
     * 源域名
     */
    @Setter
    @Getter
    private MappedProp<String> fromHost;


    /**
     * 源域名
     */
    @Setter
    @Getter
    private MappedProp<Integer> fromPort;


    //To: sip:媒体流发送者设备编码@目的域名

    //Contact: <sip:媒体流接收者设备编码@源IP地址端口>
    //后续Request将根据Contact头域的内容决定目的地的地址，同时将Contact头域的内容放到Request-URI中
    //它还可以用来指示没有在Record-Route头域中记录的Proxies的地址
    //同时它还可以被用在Redirect servers和REGISTER requests 和responses
    /**
     * 媒体流接收者设备编码
     */
    @Setter
    @Getter
    private MappedProp<String> contactDeviceId;

    /**
     * 源IP地址端口
     */
    @Setter
    @Getter
    private MappedProp<Address> contactAddress;


    //Subject:媒体流发送者设备编码：发送端媒体流序列号，媒体流接收者设备编码：接收端媒体流序列号

    //o= 媒体流接收者设备编码 0 0 IN IP4 172.20.16.3
    /**
     * original 跟fromHost 一致，源域名或ip地址
     */
    @Setter
    @Getter
    private MappedProp<String> originalHost;

    //c= connection 媒体连接ip
    @Setter
    @Getter
    private MappedProp<String> connectionHost;


    //m= 媒体接收端口 RTP/AVP 0
    /**
     * 媒体接收端口
     */
    @Setter
    @Getter
    private MappedProp<Integer> mediaPort;

    //s= Play
    /**
     * 操作类型
     */
    @Setter
    @Getter
    private MappedProp<String> sessionName;


    /**
     * 冗余字段，表明请求下一个发向谁，但是并不作为替换sip消息的依据
     */
    @Setter
    @Getter
    private InetSocketAddress toAddress;


}
