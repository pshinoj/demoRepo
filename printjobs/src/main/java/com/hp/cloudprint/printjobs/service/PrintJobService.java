package com.hp.cloudprint.printjobs.service;

import com.hp.cloudprint.printjobs.common.JobStatus;
import com.hp.cloudprint.printjobs.common.PrintJob;
import com.hp.cloudprint.printjobs.model.Job;


/**
 * Created by prabhash on 6/29/2014.
 */
public interface PrintJobService {
    public PrintJob submitJob(Job job);
    public JobStatus getJobStatus(String printJobId);
    public void updateJobStatus(String jobId, JobStatus status);
}
