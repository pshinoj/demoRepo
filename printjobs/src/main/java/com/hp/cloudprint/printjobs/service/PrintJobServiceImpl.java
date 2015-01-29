package com.hp.cloudprint.printjobs.service;

import com.hp.cloudprint.printjobs.common.JobStatus;
import com.hp.cloudprint.printjobs.common.PrintJob;
import com.hp.cloudprint.printjobs.dao.JobDAO;
import com.hp.cloudprint.printjobs.model.Job;
import com.hp.cloudprint.printjobs.queue.JobQueue;
import com.hp.cloudprint.printjobs.queue.JobQueueException;
import com.hp.cloudprint.printjobs.queue.JobQueueImpl;
import com.hp.cloudprint.printjobs.queue.QueueMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by prabhash on 6/29/2014.
 */
@Service
public class PrintJobServiceImpl implements PrintJobService {
    private static final Logger LOG = LoggerFactory.getLogger(PrintJobServiceImpl.class);
    private static final String JOB_QUEUE = "job-queue";

    @Autowired
    JobDAO jobDAO;
    @Autowired
    JobQueue queue;

    @Override
    @Transactional
    public PrintJob submitJob(Job job) {
        Job uJob = jobDAO.save(job);
        LOG.info("Job saved to database, job_id = " + uJob.getJobId());
        Long messageId = null;
        int retry = 0;
        do {
            try {
                messageId = queue.enqueue(buildQueueMessage(job));
                retry = 0;
            } catch (JobQueueException e) {
                // Queue exception occurred retry one more time
                retry++;
            }
        } while (retry > 0 && retry < 2);
        queue.close();
        LOG.info("Job enqueued to queue, message_id = " + messageId.toString());
        PrintJob printJob = new PrintJob();
        printJob.setPrintJobId(uJob.getJobId());
        printJob.setStatus(uJob.getStatus());
        printJob.setErrorCode(uJob.getErrorCode());
        printJob.setStatusMessage(generateStatusMessage(uJob.getStatus()));
        return printJob;
    }

    private String generateStatusMessage(String status) {
        String message = "";
        if (status.equals("PROCESSING")) {
            message = "The job document processing is in progress";
        } else if (status.equals("SUBMITTED")) {
            message = "The job is waiting in queue to get started";
        } else if (status.equals("COMPLETED")) {
            message = "The job printed successfully";
        } else if (status.equals("PRINTING")) {
            message = "The job printing is in progress";
        } else if (status.equals("FAILED")) {
            message = "The job failed to print due to an internal error";
        }
        return message;
    }

    @Override
    public JobStatus getJobStatus(String printJobId) {
        return  jobDAO.findStatus(printJobId);
    }

    @Override
    @Transactional
    public void updateJobStatus(String generatedId, JobStatus status) {
        LOG.info("Updating status of job {} to {}", new String[] { generatedId, status.getStatus() });
        if (status != null) {
            Job job = jobDAO.fetch(generatedId);
            job.setJobStatus(status);
            jobDAO.update(job);
        }
    }

    private QueueMessage buildQueueMessage(Job job) {
        final QueueMessage message = new QueueMessage();
        message.setPriority(job.getPriority());
        message.setQueueName(JOB_QUEUE);
        message.setData(String.valueOf(job.getId()).getBytes());
        message.setDelayBy(1);
        return message;
    }
}
