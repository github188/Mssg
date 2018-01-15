package com.fable.mssg.vo;

import java.util.List;

/**
 * @Description 扁平目录接口
 * @Author wangmeng 2017/09/01
 */
public interface FlatDir {

    String thisId();
    String parentId();
    List<FlatDir> getChildren();
}
