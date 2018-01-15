package com.fable.mssg.vo.dict;

import lombok.Builder;
import lombok.Data;

/**
 * description 字典视图类
 * @author xiejk 2017/9/20
 */
@Builder
@Data
public class VDict {
    //字典id
    String id;

    //字典名称
    String dictName;

    //字典编码
    Long dictCode;

    //字典备注
    String dictRemark;
}
