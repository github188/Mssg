package com.fable.mssg.catalog.config;

import com.fable.framework.core.config.Address;
import lombok.Data;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author: yuhl Created on 14:41 2017/9/8 0008
 */
@ConfigurationProperties(prefix = "com.fable.mssg.equipment")
@Data
public class MasterConfigProperties {

    public Master master;

    public Schedule schedule;

    public Slave slave;

    public Authorize authorize; // 客户端、设备认证配置

    public Encryption encryption; // 加密配置

    public Address remoteServer;

    @Data
    public static class Master {

        @Getter
        public String host; // master地址

        @Getter
        public int port; // master端口

        @Getter
        public String user; // master设备id

        @Getter
        public int expires; // 超时时间
    }

    @Data
    public static class Schedule {
        @Getter
        public int regInterval; // 注册信令间隔

        @Getter
        public int regDelay; // 延迟注册时间

        @Getter
        public int keepaliveInterval; // 保活信令周期

        @Getter
        public int keepaliveDelay; // 延迟保活时间

        @Getter
        public int qInterval; // 查询信令间隔

        @Getter
        public int qDelay; // 延迟查询时间

        @Getter
        public int subInterval; // 订阅信令间隔

        @Getter
        public int subDelay; // 延迟订阅时间

    }

    @Data
    public static class Slave {

        @Getter
        public String host; // slave的IP地址

        @Getter
        public int port; // 端口

        @Getter
        public String slaveId; // slave设备id
    }

    @Data
    public static class Authorize {

        @Getter
        public String realm; // 认证的realm

        @Getter
        public String nonce; // 认证的nonce

        @Getter
        public String algorithm; // 加密算法

        @Getter
        public int duration; // 数字摘要有效期-单位：S
    }

    @Data
    public static class Encryption {

        @Getter
        public String desKey;

        @Getter
        public String triDesKey;

        @Getter
        public String aesKey;

    }

}
