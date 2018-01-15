package com.fable.mssg.vo.equipment;

import lombok.Data;

/**
 * 设备管理视图类
 */
@Data
public class VEquipment {
    private String id;
    private String equipmentName;
    private String ipAddress;
    private String jkdwlx;
    private String sxjkwzlx;
    private String equipmentType;
    private String manuName;//视频厂商
}
