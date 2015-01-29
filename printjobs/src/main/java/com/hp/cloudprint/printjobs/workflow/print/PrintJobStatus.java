package com.hp.cloudprint.printjobs.workflow.print;

/**
 * Created by prabhash on 10/9/2014.
 */
public class PrintJobStatus {
    private String status;
    private String statusMessage;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatusMessage() {
        return statusMessage;
    }

    public void setStatusMessage(String statusMessage) {
        this.statusMessage = statusMessage;
    }
}
