package com.hp.cloudprint.printjobs.config;

import com.hp.cloudprint.printjobs.jobevent.EventHandler;
import com.hp.cloudprint.printjobs.queue.JobQueue;
import com.hp.cloudprint.printjobs.queue.JobQueueImpl;
import com.hp.cloudprint.printjobs.worker.PrintJobWorker;
import com.hp.cloudprint.printjobs.workflow.PrintFlow;
import com.hp.cloudprint.printjobs.workflow.RenderFlow;
import com.hp.cloudprint.printjobs.workflow.ValidateFlow;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/**
 * Created by prabhash on 7/1/2014.
 */
@Configuration
@ComponentScan(basePackages = { "com.hp.cloudprint.printjobs.worker", "com.hp.cloudprint.printjobs.workflow" })
public class CoreConfig {

    @Bean
    @Scope("prototype")
    public JobQueue jobQueue() {
        return new JobQueueImpl();
    }

    @Bean
    public PrintFlow printFlow() { return new PrintFlow(); }

    @Bean
    public RenderFlow renderFlow() { return new RenderFlow(); }

    @Bean(initMethod = "start")
    public PrintJobWorker printJobWorker() {
        return new PrintJobWorker();
    }

    @Bean
    public ValidateFlow validateFlow() { return new ValidateFlow(); }

    @Bean
    public EventHandler eventHandler() { return new EventHandler(); }

    @Bean
    public ThreadPoolTaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(10);
        executor.setMaxPoolSize(10);
        executor.setQueueCapacity(25);
        return executor;
    }

}
