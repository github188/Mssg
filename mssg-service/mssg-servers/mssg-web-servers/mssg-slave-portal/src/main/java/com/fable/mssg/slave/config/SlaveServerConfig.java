package com.fable.mssg.slave.config;

import com.fable.mssg.utils.HttpsURLConnectionUtilX;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SlaveServerConfig {
    @Bean
    @ConditionalOnProperty(value = "server.ssl.enabled")
    public HttpsUrlConnectionBean HttpsUrlConnectionBean(){
        HttpsUrlConnectionBean bean = new HttpsUrlConnectionBean();
        bean.enableHttpsClient();
        return bean;
    }

    public static class HttpsUrlConnectionBean{
        public void enableHttpsClient(){
            try {
                HttpsURLConnectionUtilX.initHttpsURLConnection();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
