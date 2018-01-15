package com.fable.mssg.security.header;

import com.fable.mssg.catalog.utils.SipConstant;
import com.fable.mssg.security.header.impl.DateHeaderImpl;
import io.pkts.buffer.Buffer;
import io.pkts.buffer.Buffers;
import io.pkts.packet.sip.header.SipHeader;

/**
 * @author: yuhl Created on 15:08 2017/11/16 0016
 */
public interface DateHeader extends SipHeader {

    Buffer NAME = Buffers.wrap("Date");

    String getDate();

    void setDate(String var1);

    DateHeader clone();

    static DateHeader create(String date) {
        return new DateHeaderImpl(date);
    }

    static DateHeader create() {
        return new DateHeaderImpl(SipConstant.current());
    }

}
