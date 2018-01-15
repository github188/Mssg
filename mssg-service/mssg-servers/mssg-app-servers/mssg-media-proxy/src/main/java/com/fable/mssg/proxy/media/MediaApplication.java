package com.fable.mssg.proxy.media;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * .
 *
 * @author stormning 2017/8/20
 * @since 1.3.0
 */
@SpringBootApplication
@ComponentScan(basePackages = "com.fable.mssg.proxy.media", excludeFilters = @ComponentScan.Filter(Configuration.class))
@EnableCaching
@EnableAsync
public class MediaApplication {

    public static void main(String[] args) {
        SpringApplication.run(MediaApplication.class, args);
    }
}
