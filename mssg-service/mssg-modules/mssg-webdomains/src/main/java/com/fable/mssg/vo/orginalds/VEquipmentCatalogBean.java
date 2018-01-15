package com.fable.mssg.vo.orginalds;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

/**
 * @author : yuhl 2017-09-01
 */
@Builder
@Data
@ApiModel("设备目录")
public class VEquipmentCatalogBean {

    @ApiModelProperty("主键")
    public String id;

    @ApiModelProperty("目录名称")
    public String dsName;

    @ApiModelProperty("设备id")
    public String deviceId;

    @ApiModelProperty("目录类型")
    public Integer dsType;

    @ApiModelProperty("厂商名称")
    public String manuName;

    @ApiModelProperty("设备型号")
    public String model;

    @ApiModelProperty("设备归属")
    public String owner;

    @ApiModelProperty("关联区域编码")
    public String civilCode;

    @ApiModelProperty("组织机构编码")
    public String block;

    @ApiModelProperty("设备地址")
    public String address;

    @ApiModelProperty("父级标识")
    public Integer parental;

    @ApiModelProperty("父级id")
    public String parentId;

    @ApiModelProperty("注册路径")
    public Integer registerWay;

    @ApiModelProperty("是否保密")
    public Integer secrecy;

    @ApiModelProperty("设备状态")
    public String status;

    @ApiModelProperty("设备业务分组id")
    public String busGroupId;

    @ApiModelProperty("登陆密码")
    public String loginPwd;

    @ApiModelProperty("业务表父级id")
    public String parent_id;

    @ApiModelProperty("媒体源设备id")
    public String mediaDeviceId;

    @ApiModelProperty("设备等级")
    public int dsLevel;

    @ApiModelProperty("经度")
    public double lng;

    @ApiModelProperty("纬度")
    public double lat;

    @ApiModelProperty("位置类型")
    public int locationType;

    @ApiModelProperty("ip地址")
    public String ipAddress;

    @ApiModelProperty("设备类型")
    public int equipType;

    @ApiModelProperty("创建者")
    public String createUser;

    @ApiModelProperty("创建时间")
    public String createTime;

    @ApiModelProperty("更新者")
    public String updateUser;

    @ApiModelProperty("更新时间")
    public String updateTime;


}
