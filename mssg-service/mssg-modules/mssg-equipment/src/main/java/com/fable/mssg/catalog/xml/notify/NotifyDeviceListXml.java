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
@XmlAccessorType(XmlAccessType.FIELD)
public class NotifyDeviceListXml {

    @XmlElement(name = "Item")
    public CatalogNotifyItemXml notifyItemXml;
}
