package com.hp.cloudprint.printjobs.workflow;

import com.hp.cloudprint.printjobs.common.JobStatus;
import com.hp.cloudprint.printjobs.dao.JobDAO;
import com.hp.cloudprint.printjobs.dao.JobLinkDAO;
import com.hp.cloudprint.printjobs.jobevent.JobEvent;
import com.hp.cloudprint.printjobs.model.Job;
import com.hp.cloudprint.printjobs.model.JobLink;
import com.hp.cloudprint.printjobs.queue.*;
import com.hp.cloudprint.printjobs.workflow.print.DeviceStatus;
import com.hp.cloudprint.printjobs.workflow.render.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

/**
 * Created by prabhash on 10/8/2014.
 */
public class RenderFlow {
    private static final Logger LOG = LoggerFactory.getLogger(RenderFlow.class);
    private static final String queueName = PJQueue.RENDER_QUEUE.toString();
    private static final VPClient vpClient = new VPClient();
    private static final RenderClient renderClient = new RenderClient();
    private static final RenderServiceUtil renderConfig = new RenderServiceUtil();
    private static final EventHandlerClient eventClient = new EventHandlerClient();

    private JobQueue queue = new JobQueueImpl();

    @Autowired
    private JobDAO jobDAO;
    @Autowired
    private JobLinkDAO jobLinkDAO;


    public void start() {
        Long messageId = 0L;
        LOG.info("Starting render flow thread and waiting for job from queue  " + queueName);
        while (true) {
            try {
                Message message = queue.dequeue(queueName);
                if (message != null) {
                    messageId = message.getId();
                    Long jobId = Long.parseLong(new String(message.getData()));
                    LOG.info("Got a job in Render Queue. Initiating rendering for job; jobId: " + jobId + " messageId: " + messageId);
                    Job job = getJob(jobId);
                    if (job == null) {
                        throw new Exception("Failed to get job details from database");
                    }
                    if (job.getStatus().equals("RENDER_PENDING")) {
                        String deviceId = job.getDeviceId();
                        DeviceStatus deviceStatus = vpClient.getDeviceStatus(deviceId);
                        if (!deviceStatus.getStatus().equals("READY")) {
                            queue.release(messageId, 1024L, 10);
                        }
                    }
                    LOG.info("Current job status is {}, statusCode: {}, description: {} ", new String[] { job.getStatus(), Long.toString(job.getErrorCode()), job.getStatusMessage() } );
                    // Do respective actions based on current status
                    switch (job.getErrorCode()) {
                        // Render pending
                        case 10200: {
                            RenderRequest renderRequest = buildRenderRequest(job);
                            JobRenderResponse response = renderClient.submitRenderJob(renderRequest);
                            if (response != null) {
                                LOG.info("Render job submitted successfully. Waiting for render process to complete");
                                JobStatus status = new JobStatus("RENDER_PROCESSING", 20202, "The document processing is in progress.");
                                job.setJobStatus(status);
                                jobDAO.update(job);
                                JobLink jobLink = getJobLinkInternal(job);
                                if (jobLink != null) {
                                    jobLink.setRenderUri(response.getJob().getRelLink("self"));
                                    jobLinkDAO.updateLink(jobLink);
                                }
                                eventClient.notifyEvent(new JobEvent(job.getCallbackUri(), status));
                                queue.release(messageId, 1024L, 10);
                            } else {
                                JobStatus status = new JobStatus("RENDER_FAILED", 20401, "Rendering request failed");
                                job.setJobStatus(status);
                                jobDAO.update(job);
                                eventClient.notifyEvent(new JobEvent(job.getCallbackUri(), status));
                                queue.release(messageId, 1024L, 0);
                            }

                            break;
                        }
                        // Render processing
                        case 20202: {
                            LOG.info("Render process job status callback wait timed out. Checking the job status directly");
                            JobLink jobLink = getJobLinkInternal(job);
                            if (jobLink != null) {
                                JobStatus status = renderClient.getJobStatus(jobLink.getRenderUri());
                                if (status != null) {
                                    if (status.getStatusCode() == 20202) {
                                        queue.release(messageId, 1024L, 10);
                                    } else {
                                        job.setJobStatus(status);
                                        jobDAO.update(job);
                                        queue.release(messageId, 1024L, 0);
                                    }
                                    eventClient.notifyEvent(new JobEvent(job.getCallbackUri(), status));
                                    LOG.info("Got job status : " + status.getStatus());
                                }
                            } else {
                                LOG.warn("No job link available to check job status. Aborting job");
                                JobStatus status = new JobStatus("RENDER_FAILED", 20401, "Job link missing, aborting job");
                                job.setJobStatus(status);
                                jobDAO.update(job);
                                eventClient.notifyEvent(new JobEvent(job.getCallbackUri(), status));
                                queue.release(messageId, 1024L, 0);
                            }
                            break;
                        }
                        // Render success
                        case 20200: {
                            LOG.info("Rendering completed successfully. Moving job to print queue");
                            moveJobToNextQueue(job.getId());
                            queue.delete(messageId);
                            break;
                        }
                        // Render failed
                        case 20401: {
                            LOG.info("Rendering failed. Aborting job");
                            queue.delete(messageId);
                            break;
                        }
                        default:
                        {
                            LOG.warn("Unknown error occurred while rendering job " + job.getErrorCode());
                            job.setJobStatus(new JobStatus("JOB_FAILED", 10501, "An unknown error occurred while processing job"));
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
        JobQueue printQueue = new JobQueueImpl();
        final QueueMessage message = new QueueMessage();
        message.setPriority(1024);
        message.setQueueName(PJQueue.PRINT_QUEUE.toString());
        message.setData(String.valueOf(jobId).getBytes());
        message.setDelayBy(1);
        int retry = 0;
        do {
            try {
                printQueue.enqueue(message);
                retry = 0;
            } catch (JobQueueException e) {
                retry++;
            }
        } while (retry > 0 && retry < 2);
    }

    private RenderRequest buildRenderRequest(Job job) {
        RenderRequest request = new RenderRequest();
        // TODO: The settings need to mapped to settings that Sierra understands
        request.setSettings(null);
        request.setOutputMime(job.getOutputType());
        request.setPriority(10);
        request.setRefId(job.getJobId());
        request.addCallback(renderConfig.getCallbackUri(job.getJobId()));
        InputMeta inputMeta = new InputMeta();
        inputMeta.setContentType(job.getContentType());
        inputMeta.setSourceUri(job.getSourceUri());
        request.addInputMeta(inputMeta);
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
