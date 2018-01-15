package com.fable.mssg.catalog.xml.notify;

import lombok.Data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author : yuhl 2017-09-01
 * 设备目录通知
 */
@Data
@XmlRootElement(name = "Notify")
@XmlAccessorType(XmlAccessType.FIELD)
public class CatalogNotifyXml {

    @XmlElement(name = "CmdType")
    public String cmdType;

    @XmlElement(name = "SN")
    public Integer sn;

    @XmlElement(name = "DeviceID")
    public String deviceId;

    @XmlElement(name = "SumNum")
    public int sumNum;

    @XmlElement(name = "DeviceList")
    public NotifyDeviceListXml notifyDeviceListXml;
}
