package com.fable.framework.proxy.util;

import lombok.Data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author : shinan
 *         文件检索
 */
@Data
@XmlRootElement(name = "Query")
@XmlAccessorType(XmlAccessType.FIELD)
public class QueryXml {
    @XmlElement(name = "CmdType")
    private String cmdType;
    @XmlElement(name = "SN")
    private String sn;
    @XmlElement(name = "DeviceID")
    private String deviceId;
    @XmlElement(name = "StartTime")
    private String startTime;
    @XmlElement(name = "EndTime")
    private String endTime;
    @XmlElement(name = "FilePath")
    private String filePath;
    @XmlElement(name = "Address")
    private String address;
    @XmlElement(name = "Secrecy")
    private String secrecy;
    @XmlElement(name = "Type")
    private String type;
    @XmlElement(name = "RecorderID")
    private String recorderId;
}
