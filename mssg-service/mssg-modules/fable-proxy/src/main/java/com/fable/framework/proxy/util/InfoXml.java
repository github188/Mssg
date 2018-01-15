package com.fable.framework.proxy.util;

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
@XmlRootElement(name = "Info")
@XmlAccessorType(XmlAccessType.FIELD)
public class InfoXml {
    @XmlElement(name = "ControlPriority")
    private String controlPriority;
}
