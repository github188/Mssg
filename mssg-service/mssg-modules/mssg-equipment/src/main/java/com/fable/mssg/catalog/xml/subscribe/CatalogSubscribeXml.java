package com.fable.mssg.catalog.xml.subscribe;

import lombok.Data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author : yuhl 2017-09-01
 * 设备目录订阅
 */
@Data
@XmlRootElement(name = "Query")
@XmlAccessorType(XmlAccessType.FIELD)
public class CatalogSubscribeXml {

    @XmlElement(name = "CmdType")
    public String cmdType;

    @XmlElement(name = "SN")
    public Integer sn;

    @XmlElement(name = "DeviceID")
    public String deviceId;

}
