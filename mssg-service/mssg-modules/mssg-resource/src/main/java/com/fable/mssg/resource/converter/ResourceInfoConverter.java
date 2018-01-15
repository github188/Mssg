package com.fable.mssg.resource.converter;

import com.fable.mssg.datasource.converter.DataSourceConverter;
import com.fable.mssg.domain.dsmanager.DataSource;
import com.fable.mssg.domain.resmanager.Resource;
import com.fable.mssg.domain.resmanager.ResourceConfig;
import com.fable.mssg.resource.vo.VResourceInfo;
import com.fable.mssg.vo.resource.SubscribeFormPrv;
import com.slyak.spring.jpa.converter.RepoBasedConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * 资源转换类
 *
 * @author xiejk 2017/9/30
 */
@Service
public class ResourceInfoConverter extends RepoBasedConverter<Resource, VResourceInfo, String> {

    @Autowired
    DataSourceConverter dataSourceConverter;

    @Override
    protected VResourceInfo internalConvert(Resource source) {

        VResourceInfo vResourceInfo = VResourceInfo.builder()
                .resCase(source.getResCase())
                .resType(source.getResType() + "")
                .phone(source.getTelPhone())
                .englishName(source.getResEngName())
                .resCode(source.getResCode())
                .resLevel(source.getResLevel() + "")
                .linkMan(source.getLinkMan())
                //审核人
                .approval(source.getApprovalMan() == null ? "" : source.getApprovalMan().getUserName())
                // 资源名称
                .resName(source.getResName())
                //资源状态
                .resStatus(ConverterConstants.getResourceStatus(source.getResStatus()))
                //编目人
                .createUser(source.getCreateUser() == null ? "" : source.getCreateUser().getLoginName())
                //目录名称
                .catalogName(source.getCatalogId() == null ? "" : source.getCatalogId().getCatalogName())
                //目录编码
                .catalogCode(source.getResCode())
                //编制单位  这边还要判断一下source.getCreateUser().getComId()是否为nnull
                .createCompany(source.getCreateUser()==null?"":
                        source.getCreateUser().getComId()==null?"":source.getCreateUser().getComId().getName())
                //图片链接地址
                .iconRoot(source.getIconRoot())
                //资源名称
                .resName(source.getResName())
                //资源描述
                .resAbstract(source.getResAbstract())
                //主题分类
                .resMain(source.getMainCategory() + "")
                //行业分类
                .resIndustry(source.getHyCategory() + "")
                //创建时间 转换为string  yyyy-MM-dd HH:mm:ss
                .submitTime(ConverterConstants.simpleDateFormat.format(source.getCreateTime()))
                //资源id
                .id(source.getId())

                .build();
        List<SubscribeFormPrv> subscribeFormPrvList = new ArrayList<>(source.getDas().size());
        for(DataSource dataSource:source.getDas()){
            ResourceConfig resourceConfig = source.getResourceConfig();
            SubscribeFormPrv subscribeFormPrv = new SubscribeFormPrv();
            subscribeFormPrv.setDsName(dataSource.getDsName());
            subscribeFormPrv.setDsId(dataSource.getId());
            subscribeFormPrv.setDsCode(dataSource.getDsCode());
            subscribeFormPrv.setDsType(dataSource.getDsType());
            subscribeFormPrv.setPid(dataSource.getStandardParentId());
            subscribeFormPrv.setHistTime(resourceConfig.getPlayBack());
            subscribeFormPrv.setDownload(resourceConfig.getDownload());
            subscribeFormPrv.setRealSnap(resourceConfig.getRealSnap());
            subscribeFormPrv.setHisSnap(resourceConfig.getHistSnap());
            subscribeFormPrv.setRealTime(resourceConfig.getRealPlay());
            subscribeFormPrv.setRecord(resourceConfig.getRecord());
            subscribeFormPrv.setRealControl(resourceConfig.getRealControl());
            subscribeFormPrv.setHistTime(resourceConfig.getPlayBack());
            subscribeFormPrvList.add(subscribeFormPrv);
        }
        vResourceInfo.setVDataSourceList(subscribeFormPrvList);
        return vResourceInfo;
    }

}
