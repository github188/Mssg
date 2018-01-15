package com.fable.mssg.catalog.xml.query;

import lombok.Data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

/**
 * @author: yuhl Created on 15:13 2017/11/2 0002
 */
@Data
@XmlAccessorType(XmlAccessType.FIELD)
public class EquipmentInfoXml {

    @XmlElement(name = "PTZType")
    public int ptzType; // 摄相机类型 1-球机 2-半球 3-枪机 4-遥控枪

    @XmlElement(name = "PositionType")
    public int positionType; // 摄像机位置类型

    @XmlElement(name = "RoomType")
    public int roomType; // 摄像机安装位置 1-室外 2-室内

    @XmlElement(name = "UseType")
    public int useType; // 摄像机用途 1-治安 2-交通 3-重点

    @XmlElement(name = "DirectionType")
    public int directionType; // 摄像机监视方位 1-东 2-西 3-南 4-北 5-东南 6-东北 7-西南 8-西北

}
