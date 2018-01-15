package com.fable.framework.proxy.util;

import lombok.Data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import java.util.List;

/**
 * @author : shinan
 *         历史文件检索
 */
@Data
@XmlAccessorType(XmlAccessType.FIELD)
public class ResponseRecordListXml {
    @XmlAttribute(name = "Num")
    public int num;
    @XmlElement(name = "Item")
    public List<RecordItemXml> itemXmlList;

}
