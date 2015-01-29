package com.hp.cloudprint.printjobs.jobevent;

import com.hp.cloudprint.printjobs.common.JobStatus;

/**
 * Created by prabhash on 10/10/2014.
 */
public class JobEvent {
    private String clientUri;
    private String status;
    private Integer statusCode;
    private String statusMessage;

    public JobEvent() {}

    public JobEvent(String clientUri, JobStatus jobStatus) {
        this.clientUri = clientUri;
        this.status = jobStatus.getStatus();
        this.statusCode = jobStatus.getStatusCode();
        this.statusMessage = jobStatus.getStatusMessage();
    }

    public String getClientUri() {
        return clientUri;
    }

    public void setClientUri(String clientUri) {
        this.clientUri = clientUri;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(Integer statusCode) {
        this.statusCode = statusCode;
    }

    public String getStatusMessage() {
        return statusMessage;
    }

    public void setStatusMessage(String statusMessage) {
        this.statusMessage = statusMessage;
    }
}
