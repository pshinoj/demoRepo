package com.hp.cloudprint.printjobs.workflow;

import com.hp.cloudprint.printjobs.common.JobStatus;
import com.hp.cloudprint.printjobs.dao.JobDAO;
import com.hp.cloudprint.printjobs.dao.JobLinkDAO;
import com.hp.cloudprint.printjobs.model.Job;
import com.hp.cloudprint.printjobs.model.JobLink;
import com.hp.cloudprint.printjobs.queue.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by prabhash on 9/22/2014.
 */
@Component
public abstract class JobWorkflow {
    private static final Logger LOG = LoggerFactory.getLogger(JobWorkflow.class);

    private String queueName;
    private Job job;
    private JobLink jobLink;
    private JobQueue jobQueue;
    Long messageId = 0L;

    @Autowired
    private JobDAO jobDAO;
    @Autowired
    private JobLinkDAO jobLinkDAO;

    public JobWorkflow(String queueName) {
        this.queueName = queueName;
        this.jobQueue = new JobQueueImpl();
    }

    public void start() {
        try {
            while (true) {
                LOG.info("Waiting for next job from queue  " + queueName);
                Message message = jobQueue.dequeue(queueName);
                if (message != null) {
                    messageId = message.getId();
                    Long jobId = Long.parseLong(new String(message.getData()));
                    LOG.info("Got a job for processing, jobId: " + jobId + " messageId: " + messageId);
                    job = getJob(jobId);
                    if (job == null) {
                        throw new Exception("Failed to get job details from database");
                    }
                    jobLink = getJobLinkInternal(job);
                    executeWorkflow();
                }
            }
        } catch (Exception e) {
            LOG.warn("Error occurred while processing message: " + messageId + " error: " + e.getMessage());
            if (messageId > 0) {
                jobQueue.delete(messageId);
            }
        } catch (JobQueueException e) {

        } finally {

        }

    }

    @Transactional
    private JobLink getJobLinkInternal(Job job) {
        jobLink = jobLinkDAO.fetchLink(String.valueOf(job.getId()));
        if (jobLink == null) {
            jobLink = new JobLink();
            jobLink.setJobId(job.getId());
            jobLink.setClientUri(job.getCallbackUri());
            jobLinkDAO.saveLink(jobLink);
        }
        return jobLink;
    }

    protected JobLink getJobLink() {
        return jobLink;
    }

    @Transactional
    protected void updateJobLink() {
        jobLinkDAO.updateLink(jobLink);
    }

    protected abstract void executeWorkflow();

    protected void moveJobToNextQueue(String nextQueue) {
        BeanstalkdJobQueue queue = new BeanstalkdJobQueue(nextQueue);
        QueueMessage message = new QueueMessage();
        message.setQueueName(nextQueue);
        message.setPriority(1024);
        message.setDelayBy(1);
        message.setData(String.valueOf(getJob().getId()).getBytes());
        queue.enqueue(message);
        this.jobQueue.delete(messageId);
    }

    protected void clearJob() {
        this.jobQueue.delete(messageId);
    }

    protected void releaseJobToQueue(int delayInSeconds) {
        this.jobQueue.release(messageId, 1000L, delayInSeconds);
    }

    @Transactional
    protected void updateJob() {
        jobDAO.update(job);
    }

    protected void setJobStatus(JobStatus status) {
        job.setStatus(status.getStatus());
        job.setErrorCode(status.getStatusCode());
        job.setStatusMessage(status.getStatusMessage());
        jobDAO.update(job);
    }

    protected Job getJob() {
        return this.job;
    }

    @Transactional
    private Job getJob(Long jobId) {
        if (jobDAO == null) {
            LOG.error("JobDAO is not initialized. Bean configuration failed.");
            throw new RuntimeException("JobDAO is not initialized properly");
        }
        Job job = null;
        try {
            LOG.info("Fetching job from database for job_id : " + jobId);
            int retryCount = 0;
            while (job == null) {
                Thread.sleep(500);
                job = jobDAO.fetch(jobId);
                if (job == null) {
                    LOG.info("No job information found for jobId : " + jobId);
                    if (retryCount == 5)
                        break;
                    Thread.sleep(500);
                    retryCount++;
                }
            }
        } catch (InterruptedException e) {
            throw new RuntimeException("Internal error occurred. Failed to complete query");
        }
        catch (Exception e) {
            LOG.warn("Transaction error: {}", new Object[] { e.getMessage() });
            e.printStackTrace();
            throw e;
        }
        return job;
    }
}
