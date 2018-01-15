package com.fable.mssg.resource.converter;

import com.fable.mssg.datasource.converter.DataSourceConverter;
import com.fable.mssg.domain.resmanager.Resource;
import com.fable.mssg.resource.vo.VResource;
import com.slyak.spring.jpa.converter.RepoBasedConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.LinkedList;

/**
 *   资源转换类
 * @author xiejk 2017/9/30
 */
@Service
public class ResourceConverter extends RepoBasedConverter<Resource,VResource,String> {

    @Override
    protected VResource internalConvert(Resource source) {

        return VResource.builder()
                .resCase(source.getResCase())
                .resType(source.getResType() + "")
                .phone(source.getTelPhone())
                .englishName(source.getResEngName())
                .resCode(source.getResCode())
                .resLevel(source.getResLevel() + "")
                .linkMan(source.getLinkMan())
                //审核人
                .approval(source.getApprovalMan()==null?"":source.getApprovalMan().getUserName())
                // 资源名称
                .resName(source.getResName())
                //资源类型
                .resStatus(ConverterConstants.getResourceStatus(source.getResStatus()))
                //编目人
                .createUser(source.getCreateUser()==null?"":source.getCreateUser().getLoginName())
                //目录名称
                .catalogName(source.getCatalogId()==null?"":source.getCatalogId().getCatalogName())
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
                .resMain(source.getMainCategory()+"")
                //行业分类
                .resIndustry(source.getHyCategory()+"")
                //创建时间 转换为string  yyyy-MM-dd HH:mm:ss
                .submitTime(ConverterConstants.simpleDateFormat.format(source.getCreateTime()))
                //资源id
                .id(source.getId())
                //.vDataSourceList(dataSourceConverter.convert(new LinkedList<>(source.getDas())))
                .build();
    }

}
