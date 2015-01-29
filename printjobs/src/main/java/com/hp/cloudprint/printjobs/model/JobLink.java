package com.hp.cloudprint.printjobs.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

/**
 * Created by prabhash on 9/22/2014.
 */
@Entity
@Table(name = "print_job_link")
public class JobLink implements Serializable {

    @Id
    @GeneratedValue
    private Long id;

    private Long jobId;
    private String clientUri;
    private String renderUri;
    private String printUri;
    private String outputUri;

    private Date createdAt;
    private Date updatedAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getJobId() {
        return jobId;
    }

    public void setJobId(Long jobId) {
        this.jobId = jobId;
    }

    public String getClientUri() {
        return clientUri;
    }

    public void setClientUri(String clientUri) {
        this.clientUri = clientUri;
    }

    public String getRenderUri() {
        return renderUri;
    }

    public void setRenderUri(String renderUri) {
        this.renderUri = renderUri;
    }

    public String getPrintUri() {
        return printUri;
    }

    public void setPrintUri(String printUri) {
        this.printUri = printUri;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getOutputUri() {
        return outputUri;
    }

    public void setOutputUri(String outputUri) {
        this.outputUri = outputUri;
    }
}
