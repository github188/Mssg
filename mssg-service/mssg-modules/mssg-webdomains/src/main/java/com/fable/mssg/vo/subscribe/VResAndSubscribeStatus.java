package com.fable.mssg.vo.subscribe;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;


/**
 * @Description
 * @Author wangmeng 2017/11/27
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class VResAndSubscribeStatus {
    String resId;

    String resCase;

    String resName;

    String resAbstract;

    String resMain;

    String resIndustry;

    String createTime;

    String iconRoot;

    String resSubscribeId;

    String resType="9";

    int state;

}
