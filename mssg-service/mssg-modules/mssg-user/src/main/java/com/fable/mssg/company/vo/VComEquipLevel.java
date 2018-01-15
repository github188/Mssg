package com.fable.mssg.company.vo;

import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * @Description
 * @Author wangmeng 2017/11/16
 */
@Data
@Builder
public class VComEquipLevel {
    String cnLevel;
    Integer comLevel;
    List<String> equipLevel;

}
