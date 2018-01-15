package com.fable.mssg.service.user;


import com.fable.mssg.domain.apprlistmanager.ApprList;
import com.fable.mssg.vo.VRegisterApprList;

import java.sql.Timestamp;
import java.util.Map;

/**
 * description  字典接口
 * @author xiejk 2017/9/30
 */
public interface ApprListService {

    //分页查询资源订阅
    Map findAllPageByCondition(int apprStatus, int size, int page, String resName, String catalogid, Timestamp searchTime);

    //查询带个审批信息
    ApprList findOneApprList(String id);

    //审批资源订阅
    boolean update(ApprList apprList);

    //分页查询注册审批
    Map findAllResigterPage(int size, int page);


    VRegisterApprList findOneVRegisterApprList(String id);


    void save(ApprList apprList);
}
