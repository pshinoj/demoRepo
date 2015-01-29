package com.hp.cloudprint.printjobs.workflow;

import com.hp.cloudprint.printjobs.common.AppConfig;
import com.hp.cloudprint.printjobs.jobevent.JobEvent;
import com.hp.cloudprint.printjobs.queue.*;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Created by prabhash on 10/10/2014.
 */
public class EventHandlerClient {
    private static final Logger LOG = LoggerFactory.getLogger(EventHandlerClient.class);
    private static final AppConfig config = new AppConfig();

    public void notifyEvent(JobEvent event) {
        if (!config.jobEventEnabled()) {
            return;
        }
        QueueMessage message  = new QueueMessage();
        message.setDelayBy(0);
        message.setPriority(1024);
        message.setQueueName(PJQueue.EVENT_QUEUE.toString());
        ObjectMapper mapper = new ObjectMapper();
        String eventJson = null;
        try {
            eventJson = mapper.writeValueAsString(event);
            message.setData(eventJson.getBytes());
        } catch (IOException e) {
            LOG.error("Failed to parse event: " + e.getMessage());
            return;
        }

        JobQueue queue = new JobQueueImpl();
        try {
            queue.enqueue(message);
        } catch (JobQueueException e) {
            LOG.error("Failed to enqueue event: " + e.getMessage());
        }
    }
}
