package com.fable.mssg.resource.vo;

import lombok.Builder;
import lombok.Data;

/**
 * @Description 树形目录
 * @Author wangmeng 2017/9/20
 */
@Data
@Builder
public class VResArea {
    private String id;
    private String areaName;
    private String kepperId;
    private String areaKepper; //资源域管理人名
    private String pid;
    private String phone;
    private String companyName;
    private String companyId;
    //private List<VResArea> children;
}
