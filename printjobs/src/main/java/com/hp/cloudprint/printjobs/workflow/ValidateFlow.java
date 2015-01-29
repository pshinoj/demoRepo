package com.hp.cloudprint.printjobs.workflow;

import com.hp.cloudprint.printjobs.common.JobStatus;
import com.hp.cloudprint.printjobs.dao.JobDAO;
import com.hp.cloudprint.printjobs.dao.JobLinkDAO;
import com.hp.cloudprint.printjobs.jobevent.JobEvent;
import com.hp.cloudprint.printjobs.model.Job;
import com.hp.cloudprint.printjobs.model.JobLink;
import com.hp.cloudprint.printjobs.queue.*;
import com.hp.cloudprint.printjobs.workflow.print.DeviceStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

/**
 * Created by prabhash on 9/22/2014.
 */
public class ValidateFlow {
    private static final Logger LOG = LoggerFactory.getLogger(ValidateFlow.class);
    private static final String queueName = PJQueue.SUBMIT_QUEUE.toString();
    private static final EventHandlerClient eventClient = new EventHandlerClient();

    @Autowired
    private JobDAO jobDAO;
    @Autowired
    private JobLinkDAO jobLinkDAO;

    private static final VPClient client = new VPClient();
    private JobQueue queue = new JobQueueImpl();

    public void start() {
        Long messageId = 0L;
        LOG.info("Starting validate flow thread and waiting for job from queue  " + queueName);
        while (true) {
            try {
                Message message = queue.dequeue(queueName);
                if (message != null) {
                    messageId = message.getId();
                    Long jobId = Long.parseLong(new String(message.getData()));
                    LOG.info("Got a job in Validate Queue. Initiating validating for job; jobId: " + jobId + " messageId: " + messageId);
                    Job job = getJob(jobId);
                    if (job == null) {
                        throw new Exception("Failed to get job details from database");
                    }
                    JobEvent event = new JobEvent(job.getCallbackUri(), new JobStatus("VALIDATING", 10201, "The job ticket validation is in progress"));
                    eventClient.notifyEvent(event);
                    String deviceId = job.getDeviceId();
                    DeviceStatus deviceStatus = client.getDeviceStatus(deviceId);
                    if (deviceStatus.getStatus().equals("READY")) {
                        Map<String, String> jobTicket = client.validateJobTicket(deviceId, job.getSettings());
                        JobStatus status;
                        if (jobTicket.size() > 0) {
                            job.setSettings(jobTicket);
                            job.setOutputType(jobTicket.get("printFormat"));
                            status = new JobStatus("RENDER_PENDING", 10200, "The job ticket validated successfully. Waiting for document processing");
                        } else {
                            status = new JobStatus("VALIDATE_FAILED", 10401, "Job validation failed. Aborting job");
                        }
                        job.setStatus(status.getStatus());
                        job.setErrorCode(status.getStatusCode());
                        job.setStatusMessage(status.getStatusMessage());
                        jobDAO.update(job);
                        JobLink jobLink = new JobLink();
                        jobLink.setJobId(job.getId());
                        jobLink.setClientUri(job.getCallbackUri());
                        jobLinkDAO.saveLink(jobLink);
                        eventClient.notifyEvent(new JobEvent(job.getCallbackUri(), status));
                        if (status.getStatusCode() == 10200) {
                            moveJobToNextQueue(job.getId());
                        }
                    } else {
                        JobStatus status = new JobStatus("ABORTED", 10406, "Job processing failed. " + deviceStatus.getStatusMessage());
                        job.setStatus(status.getStatus());
                        job.setErrorCode(status.getStatusCode());
                        job.setStatusMessage(status.getStatusMessage());
                        jobDAO.update(job);
                        eventClient.notifyEvent(new JobEvent(job.getCallbackUri(), status));
                    }
                }
        }catch(Exception e){
            LOG.warn("Error occurred while processing message: " + messageId + " error: " + e.getMessage());
            // Need to decide what to do
        }catch(JobQueueException e){
            // Retry mechanism to be designed
        }finally{
            if (messageId > 0) {
                queue.delete(messageId);
            }
        }
    }
    }

    private void moveJobToNextQueue(Long jobId) {
        LOG.info("Validation completed successfully. Moving job to render queue");
        JobQueue renderQueue = new JobQueueImpl();
        final QueueMessage message = new QueueMessage();
        message.setPriority(1024);
        message.setQueueName(PJQueue.RENDER_QUEUE.toString());
        message.setData(String.valueOf(jobId).getBytes());
        message.setDelayBy(1);
        int retry = 0;
        do {
            try {
                renderQueue.enqueue(message);
                retry = 0;
            } catch (JobQueueException e) {
                retry++;
            }
        } while (retry > 0 && retry < 2);
    }

    private Job getJob(Long jobId) {
        if (jobDAO == null) {
            LOG.error("JobDAO is not initialized. Bean configuration failed.");
            throw new RuntimeException("JobDAO is not initialized properly");
        }
        Job job = null;
        try {
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

    private JobLink getJobLinkInternal(Job job) {
        JobLink jobLink = jobLinkDAO.fetchLink(String.valueOf(job.getId()));
        if (jobLink == null) {
            jobLink = new JobLink();
            jobLink.setJobId(job.getId());
            jobLink.setClientUri(job.getCallbackUri());
            jobLinkDAO.saveLink(jobLink);
        }
        return jobLink;
    }

}
