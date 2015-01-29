package com.hp.cloudprint.printjobs.workflow;

import com.hp.cloudprint.printjobs.common.HttpClientHelper;
import com.hp.cloudprint.printjobs.common.JobStatus;
import com.hp.cloudprint.printjobs.model.Job;
import com.hp.cloudprint.printjobs.workflow.print.*;
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
import java.util.HashMap;
import java.util.Map;

/**
 * Created by prabhash on 9/22/2014.
 */
public class VPClient {
    private static final Logger LOG = LoggerFactory.getLogger(VPClient.class);
    private static final PrintServiceUtil vpConfig = new PrintServiceUtil();
    private static final HttpClient httpClient = HttpClientHelper.createClient(vpConfig.useProxy());

    public Map<String, String> validateJobTicket(String deviceId, Map<String, String> userTicket) {
        HttpPost post = new HttpPost(vpConfig.getValidateUri(deviceId));
        String ticket = "";
        ObjectMapper mapper = new ObjectMapper();
        try {
            ticket = mapper.writeValueAsString(userTicket);
        } catch (IOException e) {
            e.printStackTrace();
        }
        LOG.info("Submitting VP Validate API @ {}\n{}", new String[] {post.getURI().toString(), ticket});
        post.setEntity(new StringEntity(ticket, ContentType.APPLICATION_JSON));
        Map<String, String> defTicket = new HashMap<String, String>();
        try {
            HttpResponse response = httpClient.execute(post);
            String responseJson = HttpClientHelper.convertHttpResponseToString(response);
            LOG.info("Got response for VP Validate API; \n" + responseJson);
            ObjectMapper mapper1 = new ObjectMapper();
            defTicket = mapper1.readValue(responseJson, Map.class);
        } catch (IOException e) {
            LOG.error("Exception occurred while VP validate job ticket: " + e.getMessage());
        }
        return defTicket;
    }

    public DeviceStatus getDeviceStatus(String deviceId) {
        DeviceStatus status = new DeviceStatus("UNKNOWN", "The status is unknown");
        HttpGet get = new HttpGet(vpConfig.getDeviceStatusUri(deviceId));
        LOG.info("Submitting VP DeviceStatus API @ " + get.getURI().toString());
        try {
            HttpResponse response = httpClient.execute(get);
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode == 200) {
                String responseJson = HttpClientHelper.convertHttpResponseToString(response);
                LOG.info("Got response for VP DeviceStatus API; \n" + responseJson);
                ObjectMapper mapper = new ObjectMapper();
                status = mapper.readValue(responseJson, DeviceStatus.class);
            } else if (statusCode == 400) {
                status = new DeviceStatus("INVALID_DEVICE", "The given device id is not a valid device");
            }
        } catch (IOException e) {
            LOG.error("Exception occurred while VP get device status: " + e.getMessage());
        }
        return status;
    }

    public PrintResponse submitPrintJob(String deviceId, PrintRequest request) {
        HttpPost post = new HttpPost(vpConfig.getNotifyJobUri(deviceId));
        LOG.info("Submitting VP Print API @ " + post.getURI().toString());
        String requestJson = "";
        ObjectMapper mapper = new ObjectMapper();
        try {
            requestJson = mapper.writeValueAsString(request);
        } catch (IOException e) {
            LOG.error("Failed to map the VP print request body: " + e.getMessage());
            return null;
        }
        post.setEntity(new StringEntity(requestJson, ContentType.APPLICATION_JSON));
        PrintResponse printResponse = null;
        try {
            HttpResponse response = httpClient.execute(post);
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode == 201 || statusCode == 202 || statusCode == 200) {
                String responseJson = HttpClientHelper.convertHttpResponseToString(response);
                LOG.info("Got response for VP Print API; \n" + responseJson);
                printResponse = mapper.readValue(responseJson, PrintResponse.class);
            } else {
                LOG.warn("Print submit job API failed with status code " + statusCode);
            }
        } catch (IOException e) {
            LOG.error("Exception occurred while VP submit job: " + e.getMessage());
        }
        return printResponse;
    }

    public JobStatus getJobStatus(String printUri) {
        LOG.info("Submitting Print Job Status API @ " + printUri);
        JobStatus jobStatus = new JobStatus("UNKNOWN", 10502, "The status is unknown");
        HttpGet get = new HttpGet(printUri);

        try {
            HttpResponse response = httpClient.execute(get);
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode == 200) {
                String responseJson = HttpClientHelper.convertHttpResponseToString(response);
                LOG.info("Got response for VP print job status API; \n" + responseJson);
                ObjectMapper mapper = new ObjectMapper();
                PrintResponse printResponse = mapper.readValue(responseJson, PrintResponse.class);
                String status = printResponse.getStatus();
                if (status.equals("COMPLETED")) {
                    jobStatus = new JobStatus("PRINT_COMPLETED", 30200, "The document printed successfully");
                } else if (status.equals("PROCESSING")) {
                    jobStatus = new JobStatus("PRINT_PROCESSING", 30202, "The document printing is in progress");
                } else if (status.equals("FAILED")) {
                    jobStatus = new JobStatus("PRINT_FAILED", 30401, "The document printing failed");
                } else if (status.equals("STOPPED")) {
                    jobStatus = new JobStatus("PRINT_STOPPED", 30205, "The document printing stopped as user action needed");
                } else {
                    LOG.warn("Unknown job status received from VP: " + status);
                }
            } else {
                LOG.warn("Print job status API failed with status code " + statusCode);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return jobStatus;
    }
}
