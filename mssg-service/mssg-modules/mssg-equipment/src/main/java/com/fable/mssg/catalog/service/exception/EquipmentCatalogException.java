package com.fable.mssg.catalog.service.exception;

import com.fable.framework.web.exception.RestApiException;

/**
 * @Description
 * @Author wangmeng 2017/11/15
 */
public class EquipmentCatalogException extends RestApiException {
    public static final String EQUIPMENT_IMPORT_EXP = "8101";
    public static final String EQUIPMENT_EXPORT_EXP = "8102";
    public static final String EQUIP_ATTR_IS_NULL = "8103";

    String code;
    public EquipmentCatalogException(String code){
        this.code = code;
    }

    @Override
    public String getErrorCode() {
        return this.code;
    }
}
