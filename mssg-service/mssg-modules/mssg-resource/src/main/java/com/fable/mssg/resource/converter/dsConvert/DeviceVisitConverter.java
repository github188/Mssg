package com.fable.mssg.resource.converter.dsConvert;

import com.fable.mssg.domain.dsmanager.DeviceVist;
import com.fable.mssg.vo.VDeviceVist;
import com.slyak.spring.jpa.converter.RepoBasedConverter;
import org.springframework.stereotype.Service;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

/**
 * 用户角色转换类
 * @author  xiejk 2017/9/30
 */
@Service
public class DeviceVisitConverter extends RepoBasedConverter<DeviceVist,VDeviceVist,String> {
    @Override
    protected VDeviceVist internalConvert(DeviceVist source) {
        DateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:dd");
        return VDeviceVist.builder()
                //设备名称
                .deviceName(source.getDeviceName())
                //操作类型
                .op_type(source.getOp_type()+"")
                //访问时长
                .vistLong(source.getVistLong())
                //发生时间
                .vistTime(sdf.format(source.getVistTime()))
                .build();
    }

}
