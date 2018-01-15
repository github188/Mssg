package com.fable.mssg.catalog.xml.query;

import lombok.Data;

import javax.xml.bind.annotation.*;
import java.util.List;

/**
 * @author : yuhl 2017-09-01
 * 设备目录查询
 */
@Data
@XmlAccessorType(XmlAccessType.FIELD)
public class ResponseDeviceListXml {

    @XmlElement(name = "Item")
    public List<CatalogItemXml> itemXmlList;

}
