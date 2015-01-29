package com.hp.cloudprint.printjobs.dao;

import com.hp.cloudprint.printjobs.common.JobStatus;
import com.hp.cloudprint.printjobs.model.Job;

import java.util.List;

/**
 * Created by prabhash on 7/1/2014.
 */
public class MockJobDAOImpl implements JobDAO {

    @Override
    public Job save(Job job) {
        job.setId(1L);
        job.setJobId("01s1-1234-1234-1234");
        return job;
    }

    @Override
    public void update(Job job) {

    }

    @Override
    public Job fetch(Long jobId) {
        return null;
    }

    @Override
    public Job fetch(String generatedId) {
        return null;
    }

    @Override
    public JobStatus findStatus(Long jobId) {
        return new JobStatus(JobStatus.COMPLETED);
    }

    @Override
    public JobStatus findStatus(String generatedId) {
        return null;
    }

    @Override
    public List<Job> fetchByDevice(Long deviceId) {
        return null;
    }
}
