package com.hp.cloudprint.printjobs.workflow.print;

/**
 * Created by prabhash on 9/22/2014.
 */
public class DeviceStatus {
    public static final DeviceStatus READY = new DeviceStatus("READY", "The device is ready");
    
    private String status;
    private String statusMessage;

    public DeviceStatus(String status, String statusMessage) {
        this.status = status;
        this.statusMessage = statusMessage;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setStatusMessage(String statusMessage) {
        this.statusMessage = statusMessage;
    }

    public DeviceStatus() {}

    public String getStatus() {
        return status;
    }

    public String getStatusMessage() {
        return statusMessage;
    }
}
