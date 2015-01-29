package com.hp.cloudprint.printjobs.dao;

import com.hp.cloudprint.printjobs.common.JobStatus;
import com.hp.cloudprint.printjobs.model.Job;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by prabhash on 6/30/2014.
 */

public interface JobDAO {
    public Job save(Job job);
    public void update(Job job);
    public Job fetch(Long jobId);
    public Job fetch(String generatedId);
    public JobStatus findStatus(Long jobId);
    public JobStatus findStatus(String generatedId);
    public List<Job> fetchByDevice(Long deviceId);
}
