package com.hp.cloudprint.printjobs.dao;

import com.hp.cloudprint.printjobs.model.JobLink;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * Created by prabhash on 9/22/2014.
 */
@Repository
@Transactional
public class JobLinkDAOImpl implements JobLinkDAO {

    @Autowired
    SessionFactory sessionFactory;

    @Override
    public JobLink saveLink(JobLink jobLink) {
        jobLink.setCreatedAt(new Date());
        sessionFactory.getCurrentSession().save(jobLink);
        return jobLink;
    }

    @Override
    public JobLink fetchLink(String jobId) {
        List<JobLink> items = sessionFactory.getCurrentSession().createQuery("FROM JobLink WHERE jobId=" + jobId).list();
        if (items != null && items.size() > 0) {
            return items.get(0);
        }
        return null;
    }

    @Override
    public void updateLink(JobLink link) {
        sessionFactory.getCurrentSession().update(link);
    }
}
