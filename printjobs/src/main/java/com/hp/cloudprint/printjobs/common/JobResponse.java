package com.hp.cloudprint.printjobs.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by prabhash on 6/26/2014.
 */
public class JobResponse {
    private String jobId;
    private JobStatus jobStatus;
    private List<Map<String,String>> links;
    private List<JobAction> actions;

    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    public JobStatus getJobStatus() {
        return jobStatus;
    }

    public void setJobStatus(JobStatus jobStatus) {
        this.jobStatus = jobStatus;
    }

    public List<Map<String, String>> getLinks() {
        return links;
    }

    public void addRelLink(String rel, String href) {
        if (this.links == null){
            this.links = new ArrayList<Map<String, String>>();
        }
        Map<String, String> item = new HashMap<String, String>();
        item.put("rel", rel);
        item.put("href", href);
        this.links.add(item);
    }

    public List<JobAction> getActions() {
        return actions;
    }

    public void addJobAction(JobAction action) {
        if (this.actions == null) {
            this.actions = new ArrayList<JobAction>();
        }
        if (action != null) {
            this.actions.add(action);
        }
    }
}
