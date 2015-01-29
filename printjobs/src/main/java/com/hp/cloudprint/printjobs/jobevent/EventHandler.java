package com.hp.cloudprint.printjobs.jobevent;

import com.hp.cloudprint.printjobs.common.AppConfig;
import com.hp.cloudprint.printjobs.common.HttpClientHelper;
import com.hp.cloudprint.printjobs.common.JobStatus;
import com.hp.cloudprint.printjobs.queue.*;
import com.hp.cloudprint.printjobs.workflow.print.PrintResponse;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Created by prabhash on 10/10/2014.
 */
public class EventHandler {
    private static final Logger LOG = LoggerFactory.getLogger(EventHandler.class);
    private static final String queueName = PJQueue.EVENT_QUEUE.toString();
    private static final HttpClient httpClient = HttpClientHelper.createClient();
    private static final AppConfig config = new AppConfig();

    private JobQueue queue = new JobQueueImpl();

    public void start() {
        if (!config.jobEventEnabled()) {
            return;
        }
        Long messageId = 0L;
        LOG.info("Starting event handler thread and waiting for next event from queue " + queueName);
        while (true) {
            try {
                Message message = queue.dequeue(queueName);
                if (message != null) {
                    messageId = message.getId();
                    ObjectMapper mapper = new ObjectMapper();
                    JobEvent event = mapper.readValue(new String(message.getData()), JobEvent.class);
                    sendClientCallback(event);
                }

            } catch (Exception e) {
                   LOG.error("Failed to process job event: " + e.getMessage());
            } catch (JobQueueException e) {
                    LOG.error("Failed to dequeue job event: " + e.getMessage());
            } finally {
                if (messageId > 0) {
                    queue.delete(messageId);
                }
            }
        }
    }

    private void sendClientCallback(JobEvent event) {
        JobStatus status = new JobStatus(event.getStatus(), event.getStatusCode(), event.getStatusMessage());
        ObjectMapper mapper = new ObjectMapper();
        try {
            String statusJson = mapper.writeValueAsString(status);
            HttpPost post = new HttpPost(event.getClientUri());
            post.setEntity(new StringEntity(statusJson, ContentType.APPLICATION_JSON));
            PrintResponse printResponse = null;
            try {
                HttpResponse response = httpClient.execute(post);
                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode == 201 || statusCode == 202 || statusCode == 200) {
                    LOG.info("Job event sent to client successfully");
                } else {
                    LOG.warn("Job event request failed with status code " + statusCode);
                }
            } catch (IOException e) {
                LOG.error("Exception occurred while Job event submission: " + e.getMessage());
            }

        } catch (IOException e) {
            LOG.error("Failed to parse the status object: " + e.getMessage());
        }
    }
}
