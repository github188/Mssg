package com.fable.mssg.vo.mediainfo;

import com.fable.mssg.domain.dsmanager.Originalds;
import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * description    媒体流视图类
 * @author xiejk 2017/9/30
 */

@Builder
@Data
public class VMediaInfo {

    //媒体平台名称
    String mediaName;

    //ip地址
    String ipAddress;

    //会话端口
    Integer sessionPort;

    //所属厂商
    String manuName;

    //流媒体平台id
    String id;

    //媒体平台类型
    Long mediaType;
    //原始数据集
    List<Originalds> originalds ;
    String areaName;//域名
    Long heartTime;//心跳周期
    int auth;//鉴权
    String realm;//realm
    String singalFormat; //信令格式
    String mediaFormat;//媒体类型格式
    String remark;//备注  详细
    String deviceId;//设备id
     String gbVersion;//国标版本

}
