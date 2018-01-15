package com.fable.mssg.resource.converter.dictConverter;

import com.fable.mssg.domain.dictmanager.DictItem;
import com.fable.mssg.vo.dict.VDictItem;
import com.slyak.spring.jpa.converter.RepoBasedConverter;
import org.springframework.stereotype.Service;

/**
 * 字典转换类
 * @author xiejk 2017/9/30
 */
@Service
public class DictItemConverter extends RepoBasedConverter<DictItem,VDictItem,String> {


    @Override
    protected VDictItem internalConvert(DictItem source) {
        return  VDictItem.builder()
                //.id(source.getId())
                .dictItemCode(source.getDictItemCode())
                .dictItemName(source.getDictItemName())
                .dictItemRemark(source.getDictItemRemark())
                .build();
    }

}
