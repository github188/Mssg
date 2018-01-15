package com.fable.mssg.catalog.xml.query;

import lombok.Data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author : yuhl 2017-09-01
 * 设备目录查询
 */
@Data
@XmlAccessorType(XmlAccessType.FIELD)
public class CatalogItemXml {

    @XmlElement(name = "DeviceID")
    public String deviceId; // 设备id

    @XmlElement(name = "Name")
    public String name; // 目录名

    @XmlElement(name = "Manufacturer")
    public String manuFacturer; // 厂商

    @XmlElement(name = "Model")
    public String model; // 型号

    @XmlElement(name = "Owner")
    public String owner; // 设备归属

    @XmlElement(name = "CivilCode")
    public String civilCode; // 行政区划编码

    @XmlElement(name = "Block")
    public String block;

    @XmlElement(name = "Address")
    public String address; // 设备地址

    @XmlElement(name = "Parental")
    public int parental; // 是否有父设备 1 有 0 无

    @XmlElement(name = "ParentID")
    public String parentId; // 父设备id

    @XmlElement(name = "SafetyWay")
    public int safetyWay;

    @XmlElement(name = "RegisterWay")
    public int registerWay; // 注册路径

    @XmlElement(name = "CertNum")
    public int certNum;

    @XmlElement(name = "Certifiable")
    public String certifiable;

    @XmlElement(name = "ErrCode")
    public String errCode; // 错误码

    @XmlElement(name = "EndTime")
    public String endTime; // 结束时间

    @XmlElement(name = "Secrecy")
    public int secrecy;

    @XmlElement(name = "IPAddress")
    public String ipAddress; // ip

    @XmlElement(name = "Port")
    public Integer port; // 端口

    @XmlElement(name = "Password")
    public String password; // 密码

    @XmlElement(name = "Status")
    public String status; // 设备状态

    @XmlElement(name = "Longitude")
    public double longitude; // 经度

    @XmlElement(name = "Latitude")
    public double latitude; // 纬度

    @XmlElement(name = "BusinessGroupID")
    public String busGroupId;

    @XmlElement(name = "Info")
    public EquipmentInfoXml infoXml;

}
