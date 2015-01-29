package com.hp.cloudprint.printjobs.workflow;

import org.slf4j.Logger;

import com.hp.cloudprint.printjobs.common.QueueType;
import com.hp.cloudprint.printjobs.dao.JobDAO;
import com.hp.cloudprint.printjobs.model.Job;
import com.hp.cloudprint.printjobs.queue.BeanstalkdJobQueue;
import com.hp.cloudprint.printjobs.queue.Message;
import com.hp.cloudprint.printjobs.queue.QueueMessage;

public abstract class AbstractWorkFlow {
	
	final BeanstalkdJobQueue jobQueue = new BeanstalkdJobQueue();

	public AbstractWorkFlow() {
		super();
	}

	public void start() {
	    
	    Long messageId = 0L;
	    int retryCount = 0;
	    while (true) {
	        try {
	            getLogger().info("Waiting for next ready job in queue: "+getQueueType());
	            Message message = jobQueue.dequeue(getQueueType().name());
	            Long jobId = 0L;
	            if (message != null) {
	                messageId = message.getId();
	                getLogger().info("Message available in queue, message_id = " + messageId);
	                String jobIdStr = new String(message.getData());
	                getLogger().info("JobId in message = " + jobIdStr);
	                jobId = Long.parseLong(jobIdStr);
	                Job job = getJob(jobId);
	                if (job==null) {
	                    getLogger().warn("Job not available from database. Unknown error occurred");
	                    throw new Exception("Failed to get the job details");
	                }
	                
	                moveJobToNextState(job, messageId);
	                // TODO: Store the flow error codes for a particular job some where out of job table
	                
	                getLogger().info("Worker: Job processing for message " + messageId + " completed for job: " + jobId);
	            } else {
	                getLogger().debug("Queue Server Connection failed");
	                // Temp code. Need to handle properly
	                if (retryCount < 3) {
	                    Thread.sleep(30000);
	                    retryCount++;
	                } else {
	                    Thread.sleep(3600000);
	                }
	            }
	        } catch (Exception e) {
	            e.printStackTrace();
	            getLogger().warn("Error occurred in worker process: " + e.getMessage());
	            // This call is temporary, this is just for cleanup. Need to handle properly
	            // Based on the exception and repeat count, decision will be made to clean up the failed job
	            //jobQueue.delete(messageId);
	        }
	    }
	}
	
	private void moveJobToNextState(Job job, Long messageId) {
		String jobStatus = job.getStatus();
		String queue=jobStatus.substring(0, jobStatus.indexOf("_"));
		if(jobStatus.endsWith("ACCEPTED")){
			int status = submitJob(job);
			if(status == 200 || status == 201){
            	job.setStatus(queue+"_COMPLETE");
            	moveToNextQueue(job);
            }else{
            	job.setStatus(queue+"_FAILED");
            }
			getJobDAO().update(job);
		}else if(jobStatus.endsWith("PENDING") || jobStatus.endsWith("_SUBMIT_FAILED")){
			int status = submitJob(job);
			if(status == 200 || status == 201){
            	job.setStatus(queue+"_SUBMIT_SUCCESS");
            }else{
            	job.setStatus(queue+"_SUBMIT_FAILED");
            }
			getJobDAO().update(job);
			//release job to queue
		}else if(jobStatus.endsWith("_SUBMIT_SUCCESS") || jobStatus.endsWith("PROCESSING")){
			String status = checkJobStatus(job);
			job.setStatus(queue+"_"+status);
			getJobDAO().update(job);
			//release job to queue
		}else if(jobStatus.endsWith("SUCCESS")){
			job.setStatus(queue+"_COMPLETE");
			getJobDAO().update(job);
			moveToNextQueue(job);
			deleteFromCurrentQueue(messageId);
		}else if(jobStatus.endsWith("FAILED")){
			job.setStatus(queue+"_FAILED");
			getJobDAO().update(job);
			deleteFromCurrentQueue(messageId);
		}else{
			getLogger().info("Unexpected state !! : "+jobStatus);
		}
		
	}

	private void deleteFromCurrentQueue(long messageId) {
		jobQueue.delete(messageId);
	}

	private void moveToNextQueue(Job job) {
		BeanstalkdJobQueue tempQueue = new BeanstalkdJobQueue();
		if(getQueueType().equals(QueueType.VALIDATE_QUEUE)){
			tempQueue.enqueue(buildQueueMessage(job, QueueType.RENDER_QUEUE));
		}else if(getQueueType().equals(QueueType.RENDER_QUEUE)){
			tempQueue.enqueue(buildQueueMessage(job, QueueType.PRINT_QUEUE));
		}else{
			
		}
		tempQueue.closeClient();
	}
	
	private QueueMessage buildQueueMessage(Job job, QueueType queueType) {
        final QueueMessage message = new QueueMessage();
        message.setPriority(job.getPriority());
        message.setQueueName(queueType.name());
        message.setData(String.valueOf(job.getId()).getBytes());
        return message;
    }

	Job getJob(Long jobId) {
        if (getJobDAO() == null) {
            getLogger().error("JobDAO is not initialized. Bean configuration failed.");
            throw new RuntimeException("JobDAO is not initialized properly");
        }
        Job job = null;
        try {
            getLogger().info("Fetching job from database for job_id : " + jobId);
            int retryCount = 0;
            while (job == null) {
                Thread.sleep(500);
                job = getJobDAO().fetch(jobId);
                if (job == null) {
                    getLogger().info("No job information found for jobId : " + jobId);
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
            getLogger().warn("Transaction error: {}", new Object[] { e.getMessage() });
            e.printStackTrace();
            throw e;
        }
        return job;
    }
	
	protected abstract int submitJob(Job job);
	
	protected abstract String checkJobStatus(Job job);
	
	protected abstract int doJob(Job job);
	
	protected abstract QueueType getQueueType();

	protected abstract Logger getLogger();
	
	protected abstract JobDAO getJobDAO();

}