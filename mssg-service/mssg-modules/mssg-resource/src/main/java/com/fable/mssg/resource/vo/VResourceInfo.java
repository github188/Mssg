package com.fable.mssg.resource.vo;

import com.fable.mssg.vo.datasource.VDataSource;
import com.fable.mssg.vo.resource.SubscribeFormPrv;
import lombok.Builder;
import lombok.Data;

import java.sql.Timestamp;
import java.util.List;

/**
 * @Description
 * @Author wangmeng 2017/11/17
 */
@Data
@Builder
public class VResourceInfo {
    //资源id
    String id;

    //编目原因
    String resCase;

    //资源类型
    String resType;

    //手机号码
    String phone;

    //资源名称
    String resName;

    //资源英文名称
    String englishName;

    //资源代码
    String resCode;

    //资源等级
    String resLevel;

    //资源描述
    String resAbstract;

    //资源主题
    String resMain;

    //行业主题
    String resIndustry;

    //资源状态
    String resStatus;

    //提交时间  创建时间
    String submitTime;

    //联系人
    String linkMan;

    //创建公司
    String createCompany;

    //编目人
    String createUser;

    //创建人
    String updateUser;

    //审核人
    String approval;

    //目录名称
    String catalogName;

    //目录编码
    String catalogCode;

    //审批意见
    String idea;

    // 图片地址
    String iconRoot;

    //数据源
    List<SubscribeFormPrv> vDataSourceList;

}
