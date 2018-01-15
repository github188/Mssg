package com.fable.mssg.resource.vo.vDataSource;

import lombok.Builder;
import lombok.Data;

/**
 * description  原始数据视图类
 * @author xiejk 2017/9/30
 */

@Builder
@Data
public class VOriginalds {

    //id
    private String id;

    //设备名称
    private String dsName;

    //分类的id
    private String pid;

    private String ipAddress;//ip地址

    private  Long  dslevel;//设备等级

    private  String  locationType;//位置类型

    private  String  equipType;//设备类型

    private String manuName;//视频厂商

    private  int visitCount;//访问次数
    private String deviceId;



}
