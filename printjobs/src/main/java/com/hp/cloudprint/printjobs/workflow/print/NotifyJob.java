package com.hp.cloudprint.printjobs.workflow.print;

/**
 * Created by prabhash on 9/15/2014.
 */
public class NotifyJob {
    private String refId;
    private String contentUri;
    private String callbackUri;

    public String getRefId() {
        return refId;
    }

    public void setRefId(String refId) {
        this.refId = refId;
    }

    public String getContentUri() {
        return contentUri;
    }

    public void setContentUri(String contentUri) {
        this.contentUri = contentUri;
    }

    public String getCallbackUri() {
        return callbackUri;
    }

    public void setCallbackUri(String callbackUri) {
        this.callbackUri = callbackUri;
    }
}
