package com.fable.mssg.converter.subscribe;

import com.fable.mssg.domain.subscribemanager.SubscribePrv;
import com.fable.mssg.vo.subscribe.VSubscribePrv;
import com.slyak.spring.jpa.converter.RepoBasedConverter;
import org.springframework.stereotype.Service;

/**
 * @Description
 * @Author wangmeng 2017/10/13
 */
@Service
public class SubscribePrvConverter extends RepoBasedConverter<SubscribePrv, VSubscribePrv, String> {
    @Override
    protected VSubscribePrv internalConvert(SubscribePrv subscribePrv) {
        return VSubscribePrv.builder()
                .dsCode(subscribePrv.getDsId() == null ? ConverterConstants.noDataSource : subscribePrv.getDsId().getDsCode())
                .dsId(subscribePrv.getDsId() == null ? ConverterConstants.noDataSource : subscribePrv.getDsId().getId())
                .dsName(subscribePrv.getDsId() == null ? ConverterConstants.noDataSource : subscribePrv.getDsId().getDsName())
                .pid(subscribePrv.getDsId() == null ? ConverterConstants.noDataSource : subscribePrv.getDsId().getParentId())
                .dsLevel(subscribePrv.getDsId() == null ? 0 :
                        subscribePrv.getDsId().getDsLevel() == null ? 0 : subscribePrv.getDsId().getDsLevel())
                .dsType(subscribePrv.getDsId() == null ? 0 :
                        subscribePrv.getDsId().getDsType() == null ? 0 : subscribePrv.getDsId().getDsType())
                .download(subscribePrv.getDownload())
                .locationType(subscribePrv.getDsId() == null ? 0 : subscribePrv.getDsId().getLocationType())
                .hisEndTime(subscribePrv.getHisEndTime() == null ? "" : ConverterConstants.simpleDateFormat.format(subscribePrv.getHisEndTime()))
                .hisBeginTime(subscribePrv.getHisBeginTime() == null ? "" : ConverterConstants.simpleDateFormat.format(subscribePrv.getHisBeginTime()))
                .realEndTime(subscribePrv.getRealEndTime() == null ? "" : ConverterConstants.simpleDateFormat.format(subscribePrv.getRealEndTime()))
                .realBeginTime(subscribePrv.getRealBeginTime() == null ? "" : ConverterConstants.simpleDateFormat.format(subscribePrv.getRealBeginTime()))
                .realPlay(subscribePrv.getRealPlay())
                .playBack(subscribePrv.getPlayBack())
                .realSnap(subscribePrv.getRealSnap())
                .realControl(subscribePrv.getRealControl())
                .histSnap(subscribePrv.getHistSnap())
                .realTime(subscribePrv.getRealTime())
                .histTime(subscribePrv.getHisTime())
                .build();

    }
}
