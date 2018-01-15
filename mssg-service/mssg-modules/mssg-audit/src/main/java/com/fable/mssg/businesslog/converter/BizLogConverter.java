package com.fable.mssg.businesslog.converter;

import com.fable.mssg.domain.businesslog.BusinessLog;
import com.fable.mssg.businesslog.vo.VBusinessLog;
import com.slyak.spring.jpa.converter.RepoBasedConverter;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;

@Service
public class BizLogConverter extends RepoBasedConverter<BusinessLog, VBusinessLog, String> {

    @Override
    protected VBusinessLog internalConvert(BusinessLog source) {

        return VBusinessLog.builder()
                .companyID(source.getCompanyId() == null ? "" : source.getCompanyId().getName())
                .deviceID(source.getDsName())
                .segmentTime(source.getSegmentTime())
                .startTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(source.getStartTime()))
                .visitUser(source.getVisitUser().getUserName())
                .visitType(source.getVisitType()+"")
                .operateType(getOperateType(source.getOperateType()))
                .build();
    }

    private String getOperateType(Integer type) {

        switch (type) {

            case BusinessLog.REALTIME:
                return "实时播放";
            case BusinessLog.HISTORY:
                return "历史播放";
            case BusinessLog.REAL_CONTROL:
                return "实时控制";
            case BusinessLog.DOWNLOAD:
                return "下载";
            case BusinessLog.REAL_SNAP:
                return "实时拍照";
            case BusinessLog.REAL_RECORD:
                return "实时录像";
            case BusinessLog.HIS_SNAP:
                return "历史拍照";
            default:
                return "";
        }
    }
}
