package com.fable.mssg.vo.datasource;

import io.swagger.annotations.ApiModel;
import lombok.Builder;
import lombok.Data;


/**
 *
 * @Author wangsy
 *
 */
@Builder
@Data
@ApiModel("用户")
public class VDataSource{
	
	String id; //主键ID
	
	String ds_name; //数据源名称,
	
	String ds_code;  //数据源ID   标准定义，长度不超过20

	String ds_type; //数据源类型  1:行政区划目录  2:系统目录 3:业务分组目录 4:虚拟目录 5:设备目录 6:自定义目录

	String parent_id; //父ID  非标准定义，用于直接区分目录间的父子关系，对应主键ID。

	String media_id;  //平台ID

	String dsLevel;
	
}
