package com.fable.mssg.vo.subscribe;

import com.fable.mssg.vo.subscribe.VSubscribePrv;
import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * @Description
 * @Author wangmeng 2017/9/20
 */
@Data
@Builder
public class VSubscribeInfo {
    String id;
    //订阅人信息 从实体对象的applyUser获取
    String applyUser;
    String applyTime;
    String applyCompany;
    String phone;//电话
    String applyReason;//申请原因
    String cellPhone;//手机
    String duty;//职务

    //资源信息 从实体对象的resId(Resource)中获取
    String resType;
    String resName;
    String englishName;
    String resCode;
    String resLevel;
    String resAbstract;
    String resMain;
    String resIndustry;



    //元数据信息 从resId中的datasourceRootId获取
    //List<VDataSource> datasourceRoot;

    //请求权限 扁平目录
    List<VSubscribePrv> subscribePrvList;




}
