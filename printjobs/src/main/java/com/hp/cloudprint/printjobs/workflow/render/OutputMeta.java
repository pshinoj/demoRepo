package com.hp.cloudprint.printjobs.workflow.render;

import org.codehaus.jackson.annotate.JsonProperty;

import java.util.Date;
import java.util.Map;

/**
 * Created by prabhash on 7/10/2014.
 */
public class OutputMeta {
    private Long id;
    @JsonProperty("content_type")
    private String contentType;
    @JsonProperty("size")
    private Long sizeInBytes;
    @JsonProperty("expires")
    private Date expireAt;
    private String purpose;
    private Map<String, String> metadata;
    private Map<String, String> links;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public Long getSizeInBytes() {
        return sizeInBytes;
    }

    public void setSizeInBytes(Long sizeInBytes) {
        this.sizeInBytes = sizeInBytes;
    }

    public Date getExpireAt() {
        return expireAt;
    }

    public void setExpireAt(Date expireAt) {
        this.expireAt = expireAt;
    }

    public Map<String, String> getMetadata() {
        return metadata;
    }

    public void setMetadata(Map<String, String> metadata) {
        this.metadata = metadata;
    }

    public Map<String, String> getLinks() {
        return links;
    }

    public void setLinks(Map<String, String> links) {
        this.links = links;
    }

    public String getPurpose() {
        return purpose;
    }

    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }
}
