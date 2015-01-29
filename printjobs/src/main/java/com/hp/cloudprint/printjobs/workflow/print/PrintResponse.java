package com.hp.cloudprint.printjobs.workflow.print;

import org.codehaus.jackson.annotate.JsonIgnore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by prabhash on 10/9/2014.
 */
public class PrintResponse {
    private String jobId;
    private String refId;
    private String status;
    private String statusMessage;
    private List<Map<String, String>> links;
    private List<JobAction> actions;
    @JsonIgnore
    private HashMap<String, String> relLinks;

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    public String getJobId() {
        return jobId;
    }


    public void setRefId(String refId) {
        this.refId = refId;
    }

    public String getRefId() {
        return refId;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public void setStatusMessage(String statusMessage) {
        this.statusMessage = statusMessage;
    }

    public String getStatusMessage() {
        return statusMessage;
    }

    public List<Map<String, String>> getLinks() {
        return links;
    }

    public void setLinks(List<Map<String, String>> links) {
        this.links = links;
    }

    public void addRelLink(String rel, String href) {
        if (this.links == null) {
            this.links = new ArrayList<Map<String, String>>();
        }
        Map<String, String> link = new HashMap<String, String>();
        link.put("rel", rel);
        link.put("href", href);
        this.links.add(link);
    }

    public Map<String, String> getRelLink() {
        if (this.relLinks == null) {
            this.relLinks = new HashMap<String, String>();
            if (this.links != null) {
                for (Map<String, String> item : links) {
                    this.relLinks.put(item.get("rel"), item.get("href"));
                }
            }
        }
        return relLinks;
    }

    public String getRelLink(String rel) {
        if (this.relLinks != null && this.relLinks.containsKey(rel)) {
            return this.relLinks.get(rel);
        }
        return null;
    }

    public List<JobAction> getActions() {
        return actions;
    }

    public void setActions(List<JobAction> actions) {
        this.actions = actions;
    }

    public void addAction(JobAction action) {
        if (this.actions == null) {
            this.actions = new ArrayList<JobAction>();
        }
        this.actions.add(action);
    }
}
