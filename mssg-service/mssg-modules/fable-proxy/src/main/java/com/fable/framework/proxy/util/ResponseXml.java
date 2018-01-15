package com.fable.framework.proxy.util;

import lombok.Data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author : shinan
 *         文件检索响应
 */
@Data
@XmlRootElement(name = "Response")
@XmlAccessorType(XmlAccessType.FIELD)
public class ResponseXml {
    @XmlElement(name = "CmdType")
    private String cmdType;
    @XmlElement(name = "SN")
    private String sn;
    @XmlElement(name = "DeviceID")
    private String deviceId;
    @XmlElement(name = "Name")
    private String name;
    @XmlElement(name = "SumNum")
    private String sumNum;
    @XmlElement(name = "RecordList")
    private ResponseRecordListXml responseRecordListXml;
    @XmlElement(name = "Result")
    private String result;
}
