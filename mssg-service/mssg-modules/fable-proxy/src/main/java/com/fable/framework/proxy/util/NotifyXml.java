package com.fable.framework.proxy.util;

import lombok.Data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author : shinan
 *   历史下载完成
 */
@Data
@XmlRootElement(name = "Notify")
@XmlAccessorType(XmlAccessType.FIELD)
public class NotifyXml {
    @XmlElement(name = "CmdType")
    private String cmdType;
    @XmlElement(name = "SN")
    private String sn;
    @XmlElement(name = "DeviceID")
    private String deviceId;
    @XmlElement(name = "NotifyType")
    private String notifyType;
}
