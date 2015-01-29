package com.hp.cloudprint.printjobs.workflow.render;

import com.hp.cloudprint.printjobs.common.AppConfig;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;

/**
 * Created by prabhash on 7/22/2014.
 */
public class RenderServiceUtil extends AppConfig {

    private Boolean stubby;
    private String baseUri;

    public RenderServiceUtil() {
        super();
        this.stubby = config.getBoolean("app.sierra.stubby");
        if (!stubby) {
            baseUri = config.getString("app.sierra.uri");
        } else {
            baseUri = config.getString("app.sierra.stubby.uri");
        }
    }

    public String getPrintJobUri() {
        String printJobUri = baseUri + config.getString("app.sierra.api.render2print");
        return printJobUri;
    }

    public String getCallbackUri(String jobId) {
        String callbackUrl = config.getString("app.job.service.uri") + config.getString("app.job.render.callback.uri");
        return callbackUrl.replace("{jobId}", jobId);
    }

    public Boolean authEnabled() {
        if (!stubby) {
            return config.getBoolean("app.sierra.uri.auth_enabled");
        } else {
            return false;
        }
    }

    public String authToken() {
        return config.getString("app.sierra.uri.auth_token", "");
    }

    public Boolean useStubby() {
        return this.stubby;
    }

    public Boolean useProxy() { return config.getBoolean("app.sierra.use_proxy"); }
}
