package com.fable.mssg.vo.subscribe;

import lombok.Builder;
import lombok.Data;

/**
 * @Description 显示订阅资源的概要
 * @Author wangmeng 2017/9/20
 */
@Data
@Builder
public class VResSubscribe {

    String id;//资源订阅id
    String resCase;
    String resType;
    String phone;
    String resName;
    String englishName;
    String resCode;
    String resLevel;
    String resAbastract;
    String resMain;
    String resIndustry;
    String status;
    String applyTime;//申请订阅时间
    String createCompany;
    String linkMan; //联系人
    String cellphoneNo; //手机
    String telphone;//办公电话
    String position;//职务
    String applyCase;//申请原因
    String applyDocPath;//申请材料

}
