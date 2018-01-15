package com.fable.framework.proxy.sip.session;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.BeanUtils;

/**
 * .
 *
 * @author stormning 2017/11/19
 * @since 1.3.0
 */
public class RSnapshot extends MessageSnapshot {

    /**
     * tcp连接方式
     */
    @Setter
    @Getter
    private Setup setup;

    //a=setup:active/passive
    /**
     * RTP over TCP传输时，重用原来的TCP连接
     */
    @Setter
    @Getter
    private boolean newConnection;

    //a=connection:new
    public RSnapshot(MessageSnapshot snapshot) {
        BeanUtils.copyProperties(snapshot, this);
    }
}
