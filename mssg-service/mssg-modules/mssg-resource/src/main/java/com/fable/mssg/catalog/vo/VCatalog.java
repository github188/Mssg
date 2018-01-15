package com.fable.mssg.catalog.vo;

import lombok.Builder;
import lombok.Data;

/**
 * @Description
 * @Author wangmeng 2017/9/20
 */
@Data
@Builder
public class VCatalog {
    String id;
    String cataName;
    String cataCode;
    String companyName;
    //String companyId;//需要管理则传,不需要则不传
    String description;
    String approval;//审核人 同样需要更换则传入id,不需要则不传
    String approvalId;
    String areaId;
    String areaName;
    Integer pid;

}
