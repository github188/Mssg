package com.fable.mssg.catalog.xml.keepalive;

import lombok.Data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author: yuhl Created on 10:45 2017/9/14 0014
 */
@Data
@XmlRootElement(name = "Notify")
@XmlAccessorType(XmlAccessType.FIELD)
public class KeepaliveXml {

    @XmlElement(name = "CmdType")
    public String cmdType;

    @XmlElement(name = "SN")
    public Integer sn;

    @XmlElement(name = "DeviceID")
    public String deviceId;

    @XmlElement(name = "Status")
    public String status;

}
