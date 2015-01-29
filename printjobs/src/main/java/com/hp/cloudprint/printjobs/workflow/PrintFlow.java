package com.hp.cloudprint.printjobs.workflow;

import com.hp.cloudprint.printjobs.common.JobStatus;
import com.hp.cloudprint.printjobs.dao.JobDAO;
import com.hp.cloudprint.printjobs.dao.JobLinkDAO;
import com.hp.cloudprint.printjobs.jobevent.JobEvent;
import com.hp.cloudprint.printjobs.model.Job;
import com.hp.cloudprint.printjobs.model.JobLink;
import com.hp.cloudprint.printjobs.queue.*;
import com.hp.cloudprint.printjobs.workflow.print.*;
import com.hp.cloudprint.printjobs.workflow.render.InputMeta;
import com.hp.cloudprint.printjobs.workflow.render.JobRenderResponse;
import com.hp.cloudprint.printjobs.workflow.render.RenderRequest;
import com.hp.cloudprint.printjobs.workflow.render.RenderServiceUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by prabhash on 10/9/2014.
 */
public class PrintFlow {
    private static final Logger LOG = LoggerFactory.getLogger(PrintFlow.class);
    private static final String queueName = PJQueue.PRINT_QUEUE.toString();
    private static final VPClient vpClient = new VPClient();
    private static final PrintServiceUtil vpConfig = new PrintServiceUtil();
    private static final EventHandlerClient eventClient = new EventHandlerClient();
    private JobQueue queue = new JobQueueImpl();

    @Autowired
    private JobDAO jobDAO;
    @Autowired
    private JobLinkDAO jobLinkDAO;


    public void start() {
        Long messageId = 0L;
        LOG.info("Starting print flow thread and waiting for job from queue  " + queueName);
        while (true) {
            try {
                Message message = queue.dequeue(queueName);
                if (message != null) {
                    messageId = message.getId();
                    Long jobId = Long.parseLong(new String(message.getData()));
                    LOG.info("Got a job in Print Queue. Initiating printing for job; jobId: " + jobId + " messageId: " + messageId);
                    Job job = getJob(jobId);
                    if (job == null) {
                        throw new Exception("Failed to get job details from database");
                    }
                    LOG.info("Current job status is {}, statusCode: {}, description: {} ", new String[] { job.getStatus(), Long.toString(job.getErrorCode()), job.getStatusMessage() } );
                    // Do respective actions based on current status
                    switch (job.getErrorCode()) {
                        // Print pending
                        case 20200: {
                            PrintRequest request = buildPrintRequest(job);
                            PrintResponse response = vpClient.submitPrintJob(job.getDeviceId(), request);
                            if (response != null) {
                                LOG.info("Print job submitted successfully. Waiting for print process to complete");
                                JobStatus status = new JobStatus("PRINT_PROCESSING", 30202, "The document printing is in progress.");
                                job.setJobStatus(status);
                                jobDAO.update(job);
                                eventClient.notifyEvent(new JobEvent(job.getCallbackUri(), status));
                                JobLink jobLink = getJobLinkInternal(job);
                                if (jobLink != null) {
                                    jobLink.setPrintUri(response.getRelLink("self"));
                                    jobLinkDAO.updateLink(jobLink);
                                }
                                queue.release(messageId, 1024L, 10);
                            } else {
                                JobStatus status = new JobStatus("PRINT_FAILED", 30401, "Print request failed");
                                job.setJobStatus(status);
                                jobDAO.update(job);
                                eventClient.notifyEvent(new JobEvent(job.getCallbackUri(), status));
                                queue.release(messageId, 1024L, 0);
                            }
                            break;
                        }
                        // Print processing
                        case 30202: {
                            LOG.info("Print process job status callback wait timed out. Checking the job print status directly");
                            JobLink jobLink = getJobLinkInternal(job);
                            if (jobLink != null) {
                                JobStatus status = vpClient.getJobStatus(jobLink.getPrintUri());
                                if (status != null) {
                                    if (status.getStatusCode() == 30202) {
                                        queue.release(messageId, 1024L, 10);
                                    } else if (status.getStatusCode() == 30205) {
                                        LOG.info("The printer is waiting for user intervention. Check job status after some time");
                                        job.setJobStatus(status);
                                        jobDAO.update(job);
                                        eventClient.notifyEvent(new JobEvent(job.getCallbackUri(), status));
                                        queue.release(messageId, 1024L, 10);
                                    } else {
                                        job.setJobStatus(status);
                                        jobDAO.update(job);
                                        eventClient.notifyEvent(new JobEvent(job.getCallbackUri(), status));
                                        queue.release(messageId, 1024L, 0);
                                    }
                                    LOG.info("Got job status : " + status.getStatus());
                                } else {
                                    LOG.warn("No status available for this job. Aborting job");
                                    JobStatus status1 = new JobStatus("PRINT_FAILED", 30401, "Job status not available from VP service");
                                    job.setJobStatus(status1);
                                    jobDAO.update(job);
                                    eventClient.notifyEvent(new JobEvent(job.getCallbackUri(), status1));
                                    queue.release(messageId, 1024L, 0);
                                }
                            } else {
                                LOG.warn("No job link available to check job status. Aborting job");
                                JobStatus status = new JobStatus("PRINT_FAILED", 30401, "Job link missing, aborting job");
                                job.setJobStatus(status);
                                jobDAO.update(job);
                                eventClient.notifyEvent(new JobEvent(job.getCallbackUri(), status));
                                queue.release(messageId, 1024L, 0);
                            }
                            break;
                        }
                        // Print stopped
                        case 30205: {
                            LOG.info("Printer was paused sometime back. Checking current job status");
                            JobLink jobLink = getJobLinkInternal(job);
                            JobStatus status = vpClient.getJobStatus(jobLink.getPrintUri());
                            if (status != null) {
                                LOG.info("Got job status : " + status.getStatus());
                                if (status.getStatusCode() != 30205) {
                                    job.setJobStatus(status);
                                    jobDAO.update(job);
                                    eventClient.notifyEvent(new JobEvent(job.getCallbackUri(), status));
                                    if (status.getStatusCode() == 30202) {
                                        queue.release(messageId, 1024L, 10);
                                    } else {
                                        queue.release(messageId, 1024L, 0);
                                    }
                                }
                            } else {
                                LOG.warn("No status available for this job. Aborting job");
                                JobStatus status1 = new JobStatus("PRINT_FAILED", 30401, "Job status not available from VP service");
                                job.setJobStatus(status1);
                                jobDAO.update(job);
                                eventClient.notifyEvent(new JobEvent(job.getCallbackUri(), status1));
                                queue.release(messageId, 1024L, 0);
                            }

                            break;
                        }
                        // Print success
                        case 30200: {
                            LOG.info("Printing completed successfully. Closing job.");
                            queue.delete(messageId);
                            break;
                        }
                        // Print failed
                        case 30401: {
                            LOG.info("Printing failed. Aborting job");
                            queue.delete(messageId);
                            break;
                        }
                        default:
                        {
                            LOG.warn("Unknown error occurred while print job " + job.getErrorCode());
                            job.setJobStatus(new JobStatus("JOB_FAILED", 10502, "An unknown error occurred while printing job"));
                            jobDAO.update(job);
                            queue.delete(messageId);
                            break;
                        }
                    }
                }
            }catch(Exception e){
                LOG.warn("Error occurred while processing message: " + messageId + " error: " + e.getMessage());
                // Need to decide what to do
                if (messageId > 0) {
                    queue.delete(messageId);
                }
            }catch(JobQueueException e){
                LOG.warn("Queue error occurred while processing job: " + e.getMessage());
                // Retry mechanism to be designed
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
        message.setQueueName(PJQueue.PRINT_QUEUE.toString());
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

    private PrintRequest buildPrintRequest(Job job) {
        JobLink jobLink = getJobLinkInternal(job);
        PrintRequest request = new PrintRequest();
        NotifyJob notifyJob = new NotifyJob();
        notifyJob.setRefId(job.getJobId());
        notifyJob.setContentUri(jobLink.getOutputUri());
        notifyJob.setCallbackUri(vpConfig.getCallbackUri(job.getJobId()));
        request.setJob(notifyJob);
        request.setJobTicket(job.getSettings());
        return request;
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
        JobLink jobLink = null;
        try {
            jobLink = jobLinkDAO.fetchLink(String.valueOf(job.getId()));
            if (jobLink == null) {
                jobLink = new JobLink();
                jobLink.setJobId(job.getId());
                jobLink.setClientUri(job.getCallbackUri());
                jobLinkDAO.saveLink(jobLink);
            }
        } catch (Exception e) {
            LOG.warn("Failed to get joblink from database: " + e.getMessage());
        }
        return jobLink;
    }
}
