package com.fable.mssg.slave.converter;

import com.fable.mssg.domain.mediainfo.MediaInfo;
import com.fable.mssg.vo.mediainfo.VMediaInfo;
import com.slyak.spring.jpa.converter.RepoBasedConverter;
import org.springframework.stereotype.Service;

/**
 * 流媒体视图转换类
 * @author  xiejk 2017/9/30
 */
public class MediaInfoConverter extends RepoBasedConverter<MediaInfo,VMediaInfo,String> {
    @Override
    protected VMediaInfo internalConvert(MediaInfo source) {
        return VMediaInfo.builder()
                //厂商名称
                .manuName(source.getManuName())
                //IP地址
                .ipAddress(source.getIpAddress())
                //端口号
                .sessionPort(source.getSessionPort())
                //平台名称
                .mediaName(source.getMediaName())
                //id
                .id(source.getId())
                .mediaType(source.getMediaType())
                .areaName(source.getAreaName())
                .heartTime(source.getHeartTime())
                .remark(source.getRemark())
                .auth(source.getAuth())
                .mediaFormat(source.getMediaFormat())
                .realm(source.getRealm())
                .singalFormat(source.getSingalFormat())
                //对应的资源名称
                //.originalds(source.getDeviceIds())
                .build();


    }

}
