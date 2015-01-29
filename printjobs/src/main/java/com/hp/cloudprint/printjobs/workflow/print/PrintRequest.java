package com.hp.cloudprint.printjobs.workflow.print;

import java.util.Map;

/**
 * Created by prabhash on 9/10/2014.
 */
public class PrintRequest {
    private NotifyJob job;
    private Map<String, String> jobTicket;

    public NotifyJob getJob() {
        return job;
    }

    public void setJob(NotifyJob job) {
        this.job = job;
    }

    public Map<String, String> getJobTicket() {
        return jobTicket;
    }

    public void setJobTicket(Map<String, String> jobTicket) {
        this.jobTicket = jobTicket;
    }
}
