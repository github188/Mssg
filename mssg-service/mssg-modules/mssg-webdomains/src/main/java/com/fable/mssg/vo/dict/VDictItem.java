package com.fable.mssg.vo.dict;

import lombok.Builder;
import lombok.Data;

/**
 * description 字典项视图类
 * @author xiejk 2017/11/06
 */
@Builder
@Data
public class VDictItem {
    //字典项id
    String id;

    //字典项名称
    String dictItemName;

    //字典项编码
    Long dictItemCode;

    //字典项描述
    String dictItemRemark;
}
