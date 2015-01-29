package com.hp.cloudprint.printjobs.model;

import com.hp.cloudprint.printjobs.common.JobStatus;

import javax.persistence.*;
import java.io.*;
import java.util.Date;
import java.util.Map;

/**
 * Created by prabhash on 6/29/2014.
 */
@Entity
@Table(name = "print_job")
public class Job implements Serializable {

    @Id
    @GeneratedValue
    private Long id;

    private String uuid;
    private String name;
    private String type;
    private Integer priority;
    private Date expiresAt;
    private String deviceId;
    private String sourceUri;
    private String contentType;
    private String outputType;
    private String status;
    private Integer errorCode;
    private String statusMessage;
    private byte[] jobTicket;
    private String callbackUri;
    private Date createdAt;
    private Date updatedAt;
    @Transient
    private Map<String, String> settings;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getJobId() {
        return uuid;
    }

    public void setJobId(String uuid) {
        this.uuid = uuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getSourceUri() {
        return sourceUri;
    }

    public void setSourceUri(String sourceUri) {
        this.sourceUri = sourceUri;
    }

    public Integer getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(Integer errorCode) {
        this.errorCode = errorCode;
    }

    public byte[] getJobTicket() {
        return jobTicket;
    }

    public void setJobTicket(byte[] jobTicket) {
        ByteArrayInputStream byteArrayInputStream;
        ObjectInputStream objectInputStream;
        byteArrayInputStream = new ByteArrayInputStream(jobTicket);
        try {
            objectInputStream = new ObjectInputStream(byteArrayInputStream);
            this.settings = (Map<String, String>)objectInputStream.readObject();
            objectInputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        this.jobTicket = jobTicket;
    }

    public String getCallbackUri() {
        return callbackUri;
    }

    public void setCallbackUri(String callbackUri) {
        this.callbackUri = callbackUri;
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

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public Map<String, String> getSettings() {
        if (settings == null) {
            ByteArrayInputStream byteArrayInputStream;
            ObjectInputStream objectInputStream;
            byteArrayInputStream = new ByteArrayInputStream(this.jobTicket);
            try {
                objectInputStream = new ObjectInputStream(byteArrayInputStream);
                this.settings = (Map<String, String>)objectInputStream.readObject();
                objectInputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }

        }
        return settings;
    }

    public void setSettings(Map<String, String> settings) {
        this.settings = settings;
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ObjectOutputStream outputStream;
        try {
            outputStream = new ObjectOutputStream(byteArrayOutputStream);
            outputStream.writeObject(settings);
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        setJobTicket(byteArrayOutputStream.toByteArray());

    }

    public String getOutputType() {
        return outputType;
    }

    public void setOutputType(String outputType) {
        this.outputType = outputType;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(Date expiresAt) {
        this.expiresAt = expiresAt;
    }

    public String getStatusMessage() {
        return statusMessage;
    }

    public void setStatusMessage(String statusMessage) {
        this.statusMessage = statusMessage;
    }

    public void setJobStatus(JobStatus jobStatus) {
        this.status = jobStatus.getStatus();
        this.errorCode = jobStatus.getStatusCode();
        this.statusMessage = jobStatus.getStatusMessage();
    }
}
