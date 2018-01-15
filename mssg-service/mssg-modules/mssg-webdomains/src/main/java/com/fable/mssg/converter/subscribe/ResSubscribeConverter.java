package com.fable.mssg.converter.subscribe;

import com.fable.mssg.domain.subscribemanager.ResSubscribe;
import com.fable.mssg.vo.subscribe.VResSubscribe;
import com.slyak.spring.jpa.converter.RepoBasedConverter;
import org.springframework.stereotype.Service;

/**
 * @Description
 * @Author wangmeng 2017/10/12
 */
@Service
public class ResSubscribeConverter extends RepoBasedConverter<ResSubscribe, VResSubscribe, String> {
    @Override
    protected VResSubscribe internalConvert(ResSubscribe resSubscribe) {
        VResSubscribe vResSubscribe = VResSubscribe.builder()
                .id(resSubscribe.getId())
                .applyTime(ConverterConstants.simpleDateFormat.format(resSubscribe.getApplyTime()))//申请订阅时间
                .englishName(resSubscribe.getResId() == null ? ConverterConstants.noRes : resSubscribe.getResId().getResEngName())
                .resName(resSubscribe.getResId() == null ? ConverterConstants.noRes : resSubscribe.getResId().getResName())
                .resCase(resSubscribe.getResId() == null ? ConverterConstants.noRes : resSubscribe.getResId().getResCase())//
                .resAbastract(resSubscribe.getResId() == null ? ConverterConstants.noRes : resSubscribe.getResId().getResAbstract())
                .createCompany(resSubscribe.getComId() == null ? ConverterConstants.noCompany : resSubscribe.getComId().getName())//申请单位
                .resType(resSubscribe.getResId() == null ? ConverterConstants.noRes : ConverterConstants.getType(resSubscribe.getResId().getResType()))
                .resMain(resSubscribe.getResId() == null ? ConverterConstants.noRes :
                        resSubscribe.getResId().getMainCategory() + "")
                .resIndustry(resSubscribe.getResId() == null ? ConverterConstants.noRes :
                        resSubscribe.getResId().getMainCategory() + "")
                .status(ConverterConstants.getSubscribeStatus(resSubscribe.getState()))
                .linkMan(resSubscribe.getLinkMan())//联系人
                .position(resSubscribe.getApplyUser().getPosition())//申请人职位、
                .applyCase(resSubscribe.getApplyCause())//申请原因
                .applyDocPath(resSubscribe.getApplyDocPath())//申请资料
                .telphone(resSubscribe.getApplyUser().getTelphone())//申请人电话
                .cellphoneNo(resSubscribe.getApplyUser().getCellPhoneNumber())//申请人手机
                .build();

        return vResSubscribe;
    }


}
