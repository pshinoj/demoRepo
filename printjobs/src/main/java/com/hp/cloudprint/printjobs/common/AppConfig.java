package com.hp.cloudprint.printjobs.common;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;

/**
 * Created by prabhash on 8/5/2014.
 */
public class AppConfig {
    protected Configuration config;

    public AppConfig() {
        try {
            config = new PropertiesConfiguration("app.properties");
        } catch (ConfigurationException e) {
            e.printStackTrace();
        }
    }

    public Boolean proxyEnabled() {
        return config.getBoolean("app.http.proxy_enabled");
    }

    public String proxyHost() {
        return config.getString("app.http.proxy.host");
    }

    public Integer proxyPort() {
        return config.getInt("app.http.proxy.port");
    }

    public String getSelfUri() {
        return config.getString("app.job.service.uri");
    }

    public Boolean jobEventEnabled() { return config.getBoolean("app.job.event.support"); }
}
