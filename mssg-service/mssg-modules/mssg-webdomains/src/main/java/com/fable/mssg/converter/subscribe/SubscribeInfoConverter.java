package com.fable.mssg.converter.subscribe;

import com.fable.mssg.domain.subscribemanager.ResSubscribe;
import com.fable.mssg.vo.subscribe.VSubscribeInfo;
import com.slyak.spring.jpa.converter.RepoBasedConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.LinkedList;

/**
 * @Description
 * @Author wangmeng 2017/10/13
 */
@Service
public class SubscribeInfoConverter extends RepoBasedConverter<ResSubscribe, VSubscribeInfo, String> {

    @Autowired
    SubscribePrvConverter subscribePrvConverter;

    @Override
    protected VSubscribeInfo internalConvert(ResSubscribe resSubscribe) {
        VSubscribeInfo vSubscribeInfo = VSubscribeInfo.builder()
                .applyReason(resSubscribe.getApplyCause())
                .applyCompany(resSubscribe.getComId() == null ? ConverterConstants.noCompany : resSubscribe.getComId().getName())
                .applyTime(ConverterConstants.simpleDateFormat.format(resSubscribe.getApplyTime()))
                .applyUser(resSubscribe.getApplyUser() == null ? ConverterConstants.noUser : resSubscribe.getApplyUser().getUserName())
                .duty(resSubscribe.getDuty())
                .cellPhone(resSubscribe.getCellPhone())
                .englishName(resSubscribe.getResId() == null ? ConverterConstants.noRes : resSubscribe.getResId().getResEngName())
                .resName(resSubscribe.getResId() == null ? ConverterConstants.noRes : resSubscribe.getResId().getResName())
                .id(resSubscribe.getId())
                .phone(resSubscribe.getTelPhone())
                .resAbstract(resSubscribe.getResId() == null ? ConverterConstants.noRes : resSubscribe.getResId().getResAbstract())
                .resCode(resSubscribe.getResId() == null ? ConverterConstants.noRes : resSubscribe.getResId().getResCode())
                .resIndustry(resSubscribe.getResId() == null ? ConverterConstants.noRes :
                        resSubscribe.getResId().getHyCategory() + "")
                .resLevel(resSubscribe.getResId() == null ? ConverterConstants.noRes : resSubscribe.getResId().getResLevel() + "")
                .resType(resSubscribe.getResId() == null ? ConverterConstants.noRes : ConverterConstants.getType(resSubscribe.getResId().getResType()))
                .resMain(resSubscribe.getResId() == null ? ConverterConstants.noRes :
                        resSubscribe.getResId().getMainCategory() + "")
                .subscribePrvList(subscribePrvConverter.convert(new LinkedList<>(resSubscribe.getSubscribePrvList())))
                .build();
        return vSubscribeInfo;
    }
}
