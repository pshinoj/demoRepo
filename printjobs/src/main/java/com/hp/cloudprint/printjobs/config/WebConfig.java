package com.hp.cloudprint.printjobs.config;

import com.hp.cloudprint.printjobs.service.PrintJobService;
import com.hp.cloudprint.printjobs.service.PrintJobServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.*;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Configuration
@EnableWebMvc
@ComponentScan(basePackages = { "com.hp.cloudprint.printjobs.controller" })
public class WebConfig extends WebMvcConfigurerAdapter {

    private static final Logger LOG = LoggerFactory.getLogger(WebConfig.class);

    @Bean
    public PrintJobService jobService() {
        return new PrintJobServiceImpl();
    }

}
