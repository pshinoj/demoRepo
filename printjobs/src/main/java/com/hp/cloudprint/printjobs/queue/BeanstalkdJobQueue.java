package com.hp.cloudprint.printjobs.queue;

import com.surftools.BeanstalkClient.Client;
import com.surftools.BeanstalkClient.Job;
import com.surftools.BeanstalkClientImpl.ClientImpl;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.util.List;
import java.util.Properties;

/**
 * Created by prabhash on 7/1/2014.
 */
public class BeanstalkdJobQueue implements JobQueue {
    private static final Logger LOG = LoggerFactory.getLogger(BeanstalkdJobQueue.class);

    private static Configuration beanConfig;
    private Client beanstalkdClient = null;
    String queueName;

    public BeanstalkdJobQueue() {
        initializeClient(null);
    }
    public BeanstalkdJobQueue(String queueName) {
        this.queueName = queueName;
        initializeClient(queueName);
    }

    @Override
    public Long enqueue(QueueMessage message) {
        initializeClient(message.getQueueName());
        Long messageId = 0L;
        try {
            messageId = beanstalkdClient.put(message.getPriority(), message.getDelayBy(), message.getExpiry().intValue(), message.getData());
        } catch (Exception e) {
            LOG.error("Failed to enqueue job to queue : " + e.getMessage());
            // TODO: Close client only when it fails (later)
            closeClient();
        }
        return messageId;
    }

    @Override
    public Message dequeue(String queueName) {
        Message message = null;
        try {
            // TODO: Convert array of queueNames to comma separated string value
            beanstalkdClient.watch(queueName);
            Job job = beanstalkdClient.reserve(null);
            if (job != null) {
                message = new Message(job.getJobId(), job.getData());
            }
        } catch (Exception e) {
            LOG.error("Failed to get job from queue : " + e.getMessage());
            closeClient();
        }
        return message;
    }

    @Override
    public void release(Long messageId, Long priority, int delayInSecs) {
        if (beanstalkdClient == null) {
            initializeClient(queueName);
        }
        beanstalkdClient.release(messageId, priority, delayInSecs);
    }

    @Override
    public void pause(Long messageId, int timeOutInSecs) {
        beanstalkdClient.release(messageId, 1024L, timeOutInSecs);
    }

    @Override
    public void resume(Long messageId) {
        if (beanstalkdClient == null) {
            // TODO: Need to figure out how to do this
            beanstalkdClient.kick(1);
        }
    }

    @Override
    public void delete(Long messageId) {
        if (beanstalkdClient == null) {
            initializeClient(queueName);
        }
        try {
            beanstalkdClient.delete(messageId);
        } catch (Exception e) {
            // TODO: Failed case need to retried at least 3 times. This retry is important as it may lead to duplicate job processing
            LOG.error("Failed to delete message from queue : " + e.getMessage());
            closeClient();
        }
    }

    @Override
    public void close() {

    }

    private Client initializeClient(String queueName) {
        if (beanConfig == null) {
            try {
                beanConfig = new PropertiesConfiguration("beanstalkd.properties");
            } catch (ConfigurationException e) {
                e.printStackTrace();
            }
        }
        if (beanstalkdClient == null) {
            String host = beanConfig.getString("host");
            Integer port = beanConfig.getInt("port");
            try {
                beanstalkdClient = new ClientImpl(host, port);
                if (queueName != null && queueName.length() > 0) {
                    beanstalkdClient.useTube(queueName);
                }
            } catch (Exception e) {
                LOG.error("Failed to establish client connection with queue server {} : {}", new Object[] { host+":"+port.toString(), e.getMessage() });
            }
        }
        return beanstalkdClient;
    }

    public void closeClient() {
        if (beanstalkdClient != null) {
            try {
                beanstalkdClient.close();
            } catch (Exception e) {
                LOG.warn("Failed to close client connection : " + e.getMessage());
            }
            beanstalkdClient = null;
        }
    }
}
