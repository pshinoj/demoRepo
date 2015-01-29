package com.hp.cloudprint.printjobs.queue;

import com.surftools.BeanstalkClient.BeanstalkException;
import com.surftools.BeanstalkClient.Client;
import com.surftools.BeanstalkClient.Job;
import com.surftools.BeanstalkClientImpl.ClientImpl;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by prabhash on 10/7/2014.
 */
public class JobQueueImpl implements JobQueue {
    private static Logger LOG = LoggerFactory.getLogger(JobQueueImpl.class);
    private Client client;
    private String host;
    private int port;

    public JobQueueImpl() {
        Configuration beanConfig = null;
        try {
            beanConfig = new PropertiesConfiguration("beanstalkd.properties");
        } catch (ConfigurationException e) {
            LOG.error("Failed to read beanstalkd properties");
        }
        this.host = beanConfig.getString("host");
        this.port = beanConfig.getInt("port");
        connect();
    }

    private void connect() {
        if (this.client != null) {
            try {
                this.client.close();
            } catch (Exception e) {
                LOG.warn("Failed to close connection: " + e.getMessage());
            }
        }
        this.client = new ClientImpl(host, port);
    }

    @Override
    public Long enqueue(QueueMessage message) throws JobQueueException {
        try {
            client.useTube(message.getQueueName());
            return client.put(message.getPriority(), message.getDelayBy(), message.getExpiry().intValue(), message.getData());
        } catch (BeanstalkException e) {
            LOG.warn("Beanstalkd exception occurred: " + e.getMessage());
            connect();
            throw new JobQueueException(e.getMessage());
        }
    }

    @Override
    public Message dequeue(String queueName) throws JobQueueException {
        try {
            client.watch(queueName);
            Job job = client.reserve(null);
            if (job != null) {
                return new Message(job.getJobId(), job.getData());
            }
        } catch (BeanstalkException e) {
            LOG.warn("Beanstalkd exception occurred: " + e.getMessage());
            connect();
            throw new JobQueueException(e.getMessage());
        }
        return null;
    }

    @Override
    public void release(Long messageId, Long priority, int delayInSecs) {
        client.release(messageId, priority, delayInSecs);
    }

    @Override
    public void pause(Long messageId, int timeOutInSecs) {
        release(messageId, 1024L, timeOutInSecs);
    }

    @Override
    public void resume(Long messageId) {
        client.kick(1);
    }

    @Override
    public void delete(Long messageId) {
        client.delete(messageId);
    }

    @Override
    public void close() {
        client.close();
    }
}
