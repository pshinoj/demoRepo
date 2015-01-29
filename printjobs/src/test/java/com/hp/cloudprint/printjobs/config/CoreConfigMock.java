package com.hp.cloudprint.printjobs.config;

import com.hp.cloudprint.printjobs.queue.JobQueue;
import com.hp.cloudprint.printjobs.queue.MockJobQueue;
import org.springframework.context.annotation.Bean;

/**
 * Created by prabhash on 7/1/2014.
 */
public class CoreConfigMock {
    @Bean
    public JobQueue jobQueue() {
		int i = 1;
		i++;
        return new MockJobQueue();
    }
}
