package com.hp.cloudprint.printjobs.workflow.render;

import org.codehaus.jackson.annotate.JsonProperty;

import java.util.Map;

/**
 * Created by prabhash on 7/10/2014.
 */
public class InputMeta {
    @JsonProperty(value = "url")
    private String sourceUri;
    @JsonProperty(value = "content_type")
    private String contentType;
    @JsonProperty(value = "http_headers")
    private Map<String,String> httpHeaders;

    public String getSourceUri() {
        return sourceUri;
    }

    public void setSourceUri(String sourceUri) {
        this.sourceUri = sourceUri;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public Map<String, String> getHttpHeaders() {
        return httpHeaders;
    }

    public void setHttpHeaders(Map<String, String> httpHeaders) {
        this.httpHeaders = httpHeaders;
    }

}
