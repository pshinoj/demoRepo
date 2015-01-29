package com.hp.cloudprint.printjobs.coordinator;

import com.hp.cloudprint.printjobs.common.JobFlowType;
import com.hp.cloudprint.printjobs.common.JobStatusInternal;
import org.codehaus.jackson.annotate.JsonIgnore;

/**
 * Created by prabhash on 8/20/2014.
 */
public class JobInfo {
    private Long messageId;
    @JsonIgnore
    private Long jobId;
    private JobFlowType flowType;
    private JobStatusInternal jobStatus;

    public Long getMessageId() {
        return messageId;
    }

    public void setMessageId(Long messageId) {
        this.messageId = messageId;
    }

    public Long getJobId() {
        return jobId;
    }

    public void setJobId(Long jobId) {
        this.jobId = jobId;
    }

    public JobFlowType getFlowType() {
        return flowType;
    }

    public void setFlowType(JobFlowType flowType) {
        this.flowType = flowType;
    }

    public JobStatusInternal getJobStatus() {
        return jobStatus;
    }

    public void setJobStatus(JobStatusInternal jobStatus) {
        this.jobStatus = jobStatus;
    }
}
