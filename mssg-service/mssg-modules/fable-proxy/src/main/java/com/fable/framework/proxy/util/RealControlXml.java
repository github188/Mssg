package com.fable.mssg.catalog.xml.query;

import com.fable.framework.proxy.util.InfoXml;
import lombok.Data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author : shinan
 *         实时控制
 */
@Data
@XmlRootElement(name = "Control")
@XmlAccessorType(XmlAccessType.FIELD)
public class RealControlXml {

    @XmlElement(name = "CmdType")
    private String cmdType;

    @XmlElement(name = "DeviceID")
    private String deviceId;

    @XmlElement(name = "SN")
    private String sn;

    @XmlElement(name = "PTZCmd")
    private String ptzCmd;

    @XmlElement(name = "Info")
    private InfoXml infoXml;

}
