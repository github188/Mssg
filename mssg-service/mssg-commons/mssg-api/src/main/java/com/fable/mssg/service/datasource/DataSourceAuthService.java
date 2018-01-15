package com.fable.mssg.service.datasource;

import com.fable.mssg.bean.AuthInfo;
import com.fable.mssg.bean.DataSourceInfo;

/**
 * @Description
 * @Author wangmeng 2017/11/7
 */
public interface DataSourceAuthService {
    AuthInfo isAuth(String sipId, String dsId, Integer operateType, Long current, Long begin, Long end,boolean isThird);
    DataSourceInfo getDataSourceInfo(String dataSourceId);
}
