package com.fable.mssg.slave;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

/**
 * .
 *
 * @author stormning 2017/8/20
 * @since 1.3.0
 */
@EnableWebMvc
@EnableCaching
@EnableScheduling
@ComponentScan(basePackages = {"com.fable.framework.web", "com.fable.mssg"},excludeFilters = @ComponentScan.Filter(Configuration.class))
@SpringBootApplication
@EnableAsync
@EnableJpaAuditing
@ServletComponentScan("com.fable.mssg.slave")//session监听
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

}
