package com.fable.mssg.bean;

/**
 * @Description
 * @Author wangmeng 2017/10/12
 */

import lombok.Data;

import java.io.Serializable;

/**
 * 订阅查询条件
 */
@Data
public class SubscribeCondition implements Serializable{
    String time;
    String catalogId;
    String resName;
    String state;
    String approvalId;
}
