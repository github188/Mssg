package com.fable.mssg.proxy.sip;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * .
 *
 * @author stormning 2017/8/20
 * @since 1.3.0
 */
@SpringBootApplication
@EnableAsync
@ComponentScan(basePackages = {"com.fable.framework.web", "com.fable.mssg.proxy.sip"}, excludeFilters = @ComponentScan.Filter(Configuration.class))
@EnableScheduling
@EnableCaching
public class SipApplication {

    public static void main(String[] args) {
        SpringApplication.run(SipApplication.class, args);
    }
}
