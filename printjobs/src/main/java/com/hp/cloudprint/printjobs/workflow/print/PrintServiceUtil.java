package com.hp.cloudprint.printjobs.workflow.print;

import com.hp.cloudprint.printjobs.common.AppConfig;

/**
 * Created by prabhash on 9/22/2014.
 */
public class PrintServiceUtil extends AppConfig {
    public Boolean stubby = false;
    private String baseUri;

    public PrintServiceUtil() {
        super();
        stubby = config.getBoolean("app.vp.stubby");
        if (stubby) {
            baseUri = config.getString("app.vp.stubby.uri");
        } else {
            baseUri = config.getString("app.vp.uri");
        }
    }

    public String getDeviceStatusUri(String deviceId) {
        String statusUri = baseUri + config.getString("app.vp.api.device.status");
        return statusUri.replace("{deviceId}", deviceId);
    }

    public String getValidateUri(String deviceId) {
        String validateUri = baseUri + config.getString("app.vp.api.job.validate");
        return validateUri.replace("{deviceId}", deviceId);
    }

    public String getNotifyJobUri(String deviceId) {
        String notifyUri = baseUri + config.getString("app.vp.api.job.notify");
        return notifyUri.replace("{deviceId}", deviceId);
    }

    public String getCallbackUri(String jobId) {
        String callbackUrl = config.getString("app.job.service.uri") + config.getString("app.job.print.callback.uri");
        return callbackUrl.replace("{jobId}", jobId);
    }

    public Boolean useProxy() { return config.getBoolean("app.vp.use_proxy"); }
}
