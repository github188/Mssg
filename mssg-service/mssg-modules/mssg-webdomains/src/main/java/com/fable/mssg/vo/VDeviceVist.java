package com.fable.mssg.vo;

import lombok.Builder;
import lombok.Data;

/**
 * description    设备访问记录视图类
 * @author xiejk 2017/9/30
 */

@Builder
@Data
public class VDeviceVist {

    //访问时间
    String vistTime;
    //设备名称
    String deviceName;
    //访问类型
    String op_type;
    //访问时长
    Long vistLong;
}
