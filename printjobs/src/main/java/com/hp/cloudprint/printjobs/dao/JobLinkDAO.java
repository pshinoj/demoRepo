package com.hp.cloudprint.printjobs.dao;

import com.hp.cloudprint.printjobs.model.JobLink;

/**
 * Created by prabhash on 9/22/2014.
 */
public interface JobLinkDAO {
    JobLink saveLink(JobLink jobLink);
    JobLink fetchLink(String jobId);
    void updateLink(JobLink link);
}
