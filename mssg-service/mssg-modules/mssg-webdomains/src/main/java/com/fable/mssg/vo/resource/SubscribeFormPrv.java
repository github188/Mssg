package com.fable.mssg.vo.resource;

import lombok.Data;

/**
 * @Description
 * @Author wangmeng 2017/12/8
 */
@Data
public class SubscribeFormPrv{
    String dsId;
    String dsName;
    int download;
    int hisSnap;
    int realControl;
    int realSnap;
    int record;
    String histTime;
    String realTime;
    String dsCode;
    String pid;
    Integer dsType;
}
