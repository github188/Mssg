package com.fable.mssg.bean;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

/**
 * @Description
 * @Author wangmeng 2017/11/9
 */
@Data
@Builder
public class DataSourceInfo implements Serializable {
    String id;
    String dsCode;
    String mediaCode;
    String mediaIp;
    Integer mediaPort;
}
