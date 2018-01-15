package com.fable.framework.proxy.sip.session;

import com.fable.framework.proxy.sip.Address;
import org.springframework.beans.BeanUtils;

import java.net.InetSocketAddress;

/**
 * .
 *
 * @author stormning 2017/11/19
 * @since 1.3.0
 */
public class ReSnapshot extends MessageSnapshot {
    public ReSnapshot(MessageSnapshot snapshot) {
        BeanUtils.copyProperties(snapshot, this);
    }


    //      应答中的 From 头域必须和请求中的 From 头域相等。
//      应答中的 Call-ID 头域必须和请求中的 Call-ID 头域相等。
//      应答中的 Cseq 头域必须和请求中的 Cseq 头域相等。
//      应答中的 Via 头域必须和请求中的 Via 头域相等，而且顺序也必须相等。
//      如果请求中包含了 To tag，那么应答中的 To 头域必须和请求中的 To 头域相等。
    public ReSnapshot(RSnapshot rSnapshot) {
        //CallId
        setCallId(rSnapshot.getCallId().reverse());

        //From
        setFromHost(rSnapshot.getFromHost().reverse());
        setFromDeviceId(rSnapshot.getFromDeviceId().reverse());
        setFromPort(rSnapshot.getFromPort().reverse());

        //TO
        setToHost(rSnapshot.getToHost().reverse());
        if (rSnapshot.getToPort() != null) {
            setToPort(rSnapshot.getToPort().reverse());
        }
        setToDeviceId(rSnapshot.getToDeviceId().reverse());

        MappedProp<Address> contactAddress = rSnapshot.getContactAddress();
        if (contactAddress != null) {
            Address input = contactAddress.getInput();
            setToAddress(new InetSocketAddress(input.getHost(), input.getPort()));
            //Contact
            setContactAddress(contactAddress.reverse());
            setContactDeviceId(rSnapshot.getContactDeviceId() == null ? null : rSnapshot.getContactDeviceId().reverse());
        }

        //Via
        setViaHost(rSnapshot.getViaHost().reverse());
        setViaPort(rSnapshot.getViaPort().reverse());

    }
}
