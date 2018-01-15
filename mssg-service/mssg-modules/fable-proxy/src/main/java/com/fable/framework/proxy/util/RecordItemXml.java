package com.fable.framework.proxy.util;

import lombok.Data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

/**
 * @author : shinan
 *         历史文件检索
 */
@Data
@XmlAccessorType(XmlAccessType.FIELD)
public class RecordItemXml {
    @XmlElement(name = "DeviceID")
    private String deviceId;
    @XmlElement(name = "Name")
    private String name;
    @XmlElement(name = "FilePath")
    private String filePath;
    @XmlElement(name = "Address")
    private String address;
    @XmlElement(name = "StartTime")
    private String startTime;
    @XmlElement(name = "EndTime")
    private String endTime;
    @XmlElement(name = "Secrecy")
    private String secrecy;
    @XmlElement(name = "Type")
    private String type;
    @XmlElement(name = "RecorderID")
    private String recordedId;
    @XmlElement(name = "FileSize")
    private String fileSize;
}
