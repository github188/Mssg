package com.fable.mssg.security.header.impl;

import com.fable.mssg.security.header.DateHeader;
import io.pkts.buffer.Buffer;
import io.pkts.buffer.Buffers;
import io.pkts.packet.sip.header.impl.SipHeaderImpl;

/**
 * @author: yuhl Created on 15:12 2017/11/16 0016
 */
public class DateHeaderImpl extends SipHeaderImpl implements DateHeader {

    private String date;

    public DateHeaderImpl(String value) {
        super(DateHeader.NAME, (Buffer)null);
        this.date = value;
    }

    @Override
    public String getDate() {
        return this.date;
    }

    @Override
    public DateHeader clone() {
        return new DateHeaderImpl(this.date);
    }

    @Override
    public void setDate(String var1) {
        this.date = var1;
    }

    @Override
    public Buffer getValue() {
        return Buffers.wrap(this.date);
    }

    public DateHeader ensure() {
        return this;
    }
}
