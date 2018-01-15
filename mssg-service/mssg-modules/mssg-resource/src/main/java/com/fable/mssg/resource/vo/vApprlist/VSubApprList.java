package com.fable.mssg.resource.vo.vApprlist;

import lombok.Data;

/**
 * description    订阅审批视图类
 * @author xiejk 2017/11/8
 */


@Data
public class VSubApprList {
    String pubTime;//发布时间
    String resName;//资源名称
    String resType;//资源类型
    String resIcon;//资源图片路径
    String resAbstract;//资源描述
    String mainCategory;//主题分类
    String hyCategory;//行业分类
    String resStatus;//资源状态
    String id;//审核表id
    String resid;//资源id
    String apprmaterial;//申请材料
    String apprStatus;
    String applyMan;
    String applyCompany;
}
