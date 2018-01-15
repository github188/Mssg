package com.fable.mssg.resource.converter.dictConverter;

import com.fable.mssg.domain.dictmanager.Dict;
import com.fable.mssg.vo.dict.VDict;
import com.slyak.spring.jpa.converter.RepoBasedConverter;
import org.springframework.stereotype.Service;

/**
 * 字典转换类
 * @author xiejk 2017/9/30
 */
@Service
public class DictConverter extends RepoBasedConverter<Dict,VDict,String> {


    @Override
    protected VDict internalConvert(Dict source) {
        return  VDict.builder()
                .dictName(source.getDictName()) //名称
                .dictCode(source.getDictCode())
                .dictRemark(source.getDictRemark())
                .id(source.getId())
                .build();
    }

}
