package com.fable.mssg.config;

import lombok.Data;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author: yuhl Created on 14:41 2017/10/27 0008
 */
@ConfigurationProperties(prefix = "com.fable.mssg.resource")
@Data
public class CommonConfigProperties {

    public Master master;

    @Data
    public static class Master {

        @Getter
        public String host; // master地址

        @Getter
        public int port; // master端口

        @Getter
        public String user; // master设备id
    }

}
