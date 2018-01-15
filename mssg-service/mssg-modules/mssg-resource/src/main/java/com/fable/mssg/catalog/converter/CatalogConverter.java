package com.fable.mssg.catalog.converter;

import com.fable.mssg.catalog.vo.VCatalog;
import com.fable.mssg.domain.resmanager.Catalog;
import com.fable.mssg.resource.converter.ConverterConstants;
import com.slyak.spring.jpa.converter.RepoBasedConverter;
import org.springframework.stereotype.Service;

/**
 * @Description
 * @Author wangmeng 2017/10/10
 */
@Service
public class CatalogConverter extends RepoBasedConverter<Catalog, VCatalog, String> {

    @Override
    protected VCatalog internalConvert(Catalog catalog) {
        return VCatalog.builder()
                .approval(catalog.getApproval()==null?"":catalog.getApproval().getUserName())
                .approvalId(catalog.getApproval()==null?"":catalog.getApproval().getId())
                .cataCode(catalog.getCatalogCode())
                .cataName(catalog.getCatalogName())
                .companyName(catalog.getComId()==null? ConverterConstants.noCompany:catalog.getComId().getName())
                .id(catalog.getId()).description(catalog.getDescription())
                .pid(-1)
                .build();
    }
}
