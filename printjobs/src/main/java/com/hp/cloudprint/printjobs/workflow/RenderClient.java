package com.hp.cloudprint.printjobs.workflow;

import com.hp.cloudprint.printjobs.common.HttpClientHelper;
import com.hp.cloudprint.printjobs.common.JobStatus;
import com.hp.cloudprint.printjobs.workflow.render.JobRenderResponse;
import com.hp.cloudprint.printjobs.workflow.render.RenderRequest;
import com.hp.cloudprint.printjobs.workflow.render.RenderResponse;
import com.hp.cloudprint.printjobs.workflow.render.RenderServiceUtil;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Created by prabhash on 9/22/2014.
 */
public class RenderClient {
    private static final Logger LOG = LoggerFactory.getLogger(RenderClient.class);
    private static final RenderServiceUtil renderConfig = new RenderServiceUtil();
    private static final HttpClient httpClient = HttpClientHelper.createClient(renderConfig.useProxy());

    public JobRenderResponse submitRenderJob(RenderRequest request) {
        JobRenderResponse response = null;
        ObjectMapper mapper = new ObjectMapper();
        String requestJson = "";
        try {
            requestJson = mapper.writeValueAsString(request);
            HttpPost post = new HttpPost(renderConfig.getPrintJobUri());
            if (renderConfig.authEnabled()) {
                post.addHeader(HttpHeaders.AUTHORIZATION, renderConfig.authToken());
            }
            LOG.info("Submitting Render Job API @ {}\n{}", new String[] {renderConfig.getPrintJobUri(), requestJson});
            post.setEntity(new StringEntity(requestJson, ContentType.APPLICATION_JSON));
            HttpResponse httpResponse = httpClient.execute(post);
            int statusCode =httpResponse.getStatusLine().getStatusCode();
            if ( statusCode == 200 || statusCode == 201 || statusCode == 202) {
                String responseJson = HttpClientHelper.convertHttpResponseToString(httpResponse);
                LOG.info("Got response for Render Job API \n" + responseJson);
                try {
                    response = mapper.readValue(responseJson, JobRenderResponse.class);
                } catch (IOException e) {
                    LOG.warn("Failed to parse render response: " + e.getMessage());
                    response = null;
                }
            } else
            {
                LOG.warn("Render Job API submit failed with status code " + statusCode);
            }
        } catch (Exception e) {
            LOG.warn("Failed to submit render job: " + e.getMessage());
        }
        return response;
    }

    public JobStatus getJobStatus(String jobUri) {
        LOG.info("Submitting Render Job Status API @ " + jobUri);
        JobStatus jobStatus = new JobStatus("UNKNOWN", 10502, "The status is unknown");
        HttpGet get = new HttpGet(jobUri);
        if (renderConfig.authEnabled()) {
            get.addHeader(HttpHeaders.AUTHORIZATION, renderConfig.authToken());
        }
        RenderResponse response = new RenderResponse();
        try {
            HttpResponse httpResponse = httpClient.execute(get);
            String responseJson = HttpClientHelper.convertHttpResponseToString(httpResponse);
            LOG.info("Got response for Render Job Status API \n" + responseJson);
            ObjectMapper mapper = new ObjectMapper();
            response = mapper.readValue(responseJson, RenderResponse.class);
            String status = response.getJob().getStatus();
            if (status.equals("SUBMITTED") || status.equals("PROCESSING")) {
               jobStatus =  new JobStatus("RENDER_PROCESSING", 20202, "The print job document conversion is in progress");
            } else if (status.equals("COMPLETED")) {
                jobStatus = new JobStatus("PRINT_PENDING", 20200, "The job document conversion completed successfully. Waiting for printing");
            } else if (status.equals("FAILED")) {
                jobStatus = new JobStatus("RENDER_FAILED", 20401, "The job document conversion failed. Aborting job");
            }
        } catch (Exception e) {
            LOG.error("Failed to get render job status: " + e.getMessage());
        }
        return jobStatus;
    }
}
