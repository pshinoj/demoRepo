package com.hp.cloudprint.printjobs.dao;

import com.hp.cloudprint.printjobs.common.JobStatus;
import com.hp.cloudprint.printjobs.model.Job;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * Created by prabhash on 6/30/2014.
 */
@Repository
@Transactional
public class JobDAOImpl implements JobDAO {

    @Autowired
    private SessionFactory sessionFactory;

    @Override
    public Job save(Job job) {
        // Save gets called only while creating a new job, so add the created date
        job.setCreatedAt(new Date());
        sessionFactory.getCurrentSession().saveOrUpdate(job);
        // TODO: Replace '01s' with shard code once sharding is supported
        String generatedId = "01s" + job.getId().toString() + "-" + UUID.randomUUID().toString();
        job.setJobId(generatedId);
        job.setStatus(JobStatus.ACCEPTED);
        job.setUpdatedAt(new Date());
        sessionFactory.getCurrentSession().update(job);
        return job;
    }

    @Override
    public void update(Job job) {
        job.setUpdatedAt(new Date());
        sessionFactory.getCurrentSession().update(job);
    }

    @Override
    public Job fetch(Long jobId) {
        return (Job)sessionFactory.getCurrentSession().get(Job.class, jobId);
    }

    @Override
    public Job fetch(String generatedId) {
        return fetch(getJobIdFromGeneratedId(generatedId));
    }

    @Override
    public JobStatus findStatus(Long jobId) {
        Job job = ((Job)sessionFactory.getCurrentSession().get(Job.class, jobId));
        // TODO: Build the job error details based on the error code obtained
        return new JobStatus(job.getStatus(), job.getErrorCode(), job.getStatusMessage());
    }

    @Override
    public JobStatus findStatus(String generatedId) {
        return findStatus(getJobIdFromGeneratedId(generatedId));
    }

    @Override
    public List<Job> fetchByDevice(Long deviceId) {
        return sessionFactory.getCurrentSession().createQuery("FROM print_job WHERE deviceId = " + deviceId).list();
    }

    private Long getJobIdFromGeneratedId(String generatedId) {
        String id = generatedId.split("-")[0].substring(3);
        return Long.parseLong(id);
    }
}
