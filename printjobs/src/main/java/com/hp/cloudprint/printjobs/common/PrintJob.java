package com.hp.cloudprint.printjobs.common;

/**
 * Created by prabhash on 8/8/2014.
 */
public class PrintJob {
    String printJobId;
    String status;
    Integer errorCode;
    private String statusMessage;

    public String getPrintJobId() {
        return printJobId;
    }

    public void setPrintJobId(String printJobId) {
        this.printJobId = printJobId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(Integer errorCode) {
        this.errorCode = errorCode;
    }

    public void setStatusMessage(String statusMessage) {
        this.statusMessage = statusMessage;
    }

    public String getStatusMessage() {
        return statusMessage;
    }
}
