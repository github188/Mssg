package com.fable.mssg.service.slave;

import java.util.List;
import java.util.Map;

/**
 * @Description
 * @Author wangmeng 2017/11/3
 */
public interface SlaveMonitorService {

    Map getSysSurvey(String comId);
    List<Map> getPopResource(Integer unit, Integer size ,String comId);
    List<Map> getTopViewUser(Integer unit, Integer size ,String comId);
}
