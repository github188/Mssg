package com.fable.framework.proxy.sip.session;

import lombok.*;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * .
 *
 * @author stormning 2017/11/8
 * @since 1.3.0
 */

@ToString
@Data
public class SipSessionKeeper implements Serializable {

    private RSnapshot requestSnapshot;

    private ReSnapshot responseSnapshot;

    private Map<Object, Object> attributes = new HashMap<Object, Object>();
}