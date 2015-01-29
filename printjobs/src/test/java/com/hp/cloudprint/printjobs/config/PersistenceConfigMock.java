package com.hp.cloudprint.printjobs.config;

import com.hp.cloudprint.printjobs.dao.JobDAO;
import com.hp.cloudprint.printjobs.dao.MockJobDAOImpl;
import org.springframework.context.annotation.Bean;

/**
 * Created by prabhash on 7/1/2014.
 */
public class PersistenceConfigMock {
    @Bean
    public JobDAO jobDAO() {
        return new MockJobDAOImpl();
    }
}
