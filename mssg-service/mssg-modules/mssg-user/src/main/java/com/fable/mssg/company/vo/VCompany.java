package com.fable.mssg.company.vo;

import lombok.Builder;
import lombok.Data;

/**
 * @Description
 * @Author wangmeng 2017/9/20
 */
@Data
@Builder
public class VCompany {
    String id;
    String companyName;
    String companyCode;
    String pid;
    Integer level;
    String email;
    String phone;
    String contacts;//联系人用户名
    String address;
    String position;  //职务
    String description;
    String ipSegement;
    String comType;
    String officePhone;
    String cnLevel;
    //List<VCompany> children;

}
