package com.fable.mssg.company.converter;

import com.fable.mssg.company.vo.VCompany;
import com.fable.mssg.domain.company.Company;
import com.slyak.spring.jpa.converter.RepoBasedConverter;
import org.springframework.stereotype.Service;

@Service
public class CompanyConverter extends RepoBasedConverter<Company, VCompany, String> {


    @Override
    protected VCompany internalConvert(Company source) {

        return VCompany.builder().companyCode(source.getCode())
                .contacts(source.getContacts())
                .email(source.getEmail())
                .id(source.getId())
                .address(source.getAddress()).level(source.getLevel())
                .phone(source.getTelphone())
                .pid(source.getPid())
                .companyName(source.getName())
                .position(source.getPosition())//职位
                .comType(source.getComType()+"")
                .description(source.getDescription())
                .ipSegement(source.getComIpSegment())
                .officePhone(source.getOfficePhone())
                .level(source.getComLevel())
                .build();
    }
}
