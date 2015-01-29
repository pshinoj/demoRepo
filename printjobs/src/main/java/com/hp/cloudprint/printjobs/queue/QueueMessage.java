package com.hp.cloudprint.printjobs.queue;

/**
 * Created by prabhash on 7/1/2014.
 */
public class QueueMessage {
    private String queueName;
    private Integer priority;
    private Long expiry;
    private MessageState state;
    private Integer delayBy;
    private byte[] data;

    public QueueMessage() {
        this.queueName = "default";
        this.priority = 65536;
        this.expiry = 120L;
        this.state = MessageState.READY;
        this.delayBy = 0;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public String getQueueName() {
        return queueName;
    }

    public void setQueueName(String queueName) {
        this.queueName = queueName;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public Long getExpiry() {
        return expiry;
    }

    public void setExpiry(Long expiry) {
        this.expiry = expiry;
    }

    public MessageState getState() {
        return state;
    }

    public void setState(MessageState state) {
        this.state = state;
    }

    public Integer getDelayBy() {
        return delayBy;
    }

    public void setDelayBy(Integer delayBy) {
        this.delayBy = delayBy;
    }

}
