package com.fable.mssg.resource.converter;

import com.fable.mssg.domain.OnLineLog;
import com.fable.mssg.resource.vo.VOnLineLog;
import com.slyak.spring.jpa.converter.RepoBasedConverter;
import org.springframework.stereotype.Service;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

/**
 * 用户访问记录转换类
 * @author  xiejk 2017/9/30
 */
@Service
public class OnLineLogConverter extends RepoBasedConverter<OnLineLog,VOnLineLog,String> {
    DateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    @Override
    protected VOnLineLog internalConvert(OnLineLog source) {
        return VOnLineLog.builder()
                //用户访问记录id
                .id(source.getId())
                //公司名称
                .companyName(source.getUser()==null?null:source.getUser().getComId().getName())
                //登录时间
                .loginTime(source.getLoginTime()==null?"":sdf.format(source.getLoginTime()))
                //下线时间
                .logoutTime(source.getLogoutTime()==null?"":sdf.format(source.getLogoutTime()))
                //用户名称
                .userName(source.getUser()==null?null:source.getUser().getUserName())
                .loginName(source.getUser()==null?"":source.getUser().getLoginName())
                //流出流量
                .onlineRate(source.getOnlineRate())
                //是否强制下线
                .istOffline(source.getIstOffline())
                //访问设备数
                .visitCount(source.getVistEquipCount()==null?0:source.getVistEquipCount())
                //用户id
                .userId(source.getUser()==null?null:source.getUser().getId())
                .loginIp(source.getLoginIp())//登录id
                .build();
    }

}
