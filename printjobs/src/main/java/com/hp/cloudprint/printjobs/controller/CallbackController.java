package com.hp.cloudprint.printjobs.controller;

import com.hp.cloudprint.printjobs.common.JobStatus;
import com.hp.cloudprint.printjobs.service.PrintJobService;
import com.hp.cloudprint.printjobs.workflow.print.PrintResponse;
import com.hp.cloudprint.printjobs.workflow.render.RenderResponse;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

/**
 * Created by prabhash on 8/8/2014.
 */
@RestController
@RequestMapping(value = "/print")
public class CallbackController {
    private static final Logger LOG = LoggerFactory.getLogger(CallbackController.class);

    @Autowired
    PrintJobService jobService;

    @RequestMapping(value = "/jobs/{jobId}/renderStatusCallback", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public void acceptRenderStatus(@RequestBody String jsonStatus, @PathVariable String jobId) {
        LOG.info("Got render callback for jobId {} : \n{}", new Object[] {jobId, jsonStatus});
        ObjectMapper mapper = new ObjectMapper();
        try {
            JobStatus status = null;
            RenderResponse response = mapper.readValue(jsonStatus, RenderResponse.class);
            if (response.getJob().getStatus().equals("COMPLETED")) {
                status = new JobStatus("PRINT_PENDING", 20200, "The job document conversion completed successfully. Waiting for printing");
            } else if (response.getJob().getStatus().equals("FAILED")) {
                status = new JobStatus("RENDER_FAILED", 20401, "The job document conversion failed. Aborting job");
            } else if (response.getJob().getStatus().equals("PROCESSING")) {
                status = new JobStatus("RENDER_PROCESSING", 20202, "The print job document conversion is in progress");
            }
           jobService.updateJobStatus(jobId, status);
        } catch (IOException e) {
            LOG.warn("Exception occurred while processing render status callback: " + e.getMessage());
        }
    }

    @RequestMapping(value = "/jobs/{jobId}/printStatusCallback", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public void acceptPrintStatus(@RequestBody String jsonStatus, @PathVariable String jobId) {
        LOG.info("Got print callback for jobId {} : \n{}", new Object[] {jobId, jsonStatus});
        ObjectMapper mapper = new ObjectMapper();
        try {
            PrintResponse printResponse = mapper.readValue(jsonStatus, PrintResponse.class);
            String status = printResponse.getStatus();
            JobStatus jobStatus = null;
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
            jobService.updateJobStatus(jobId, jobStatus);
        } catch (IOException e) {
            LOG.warn("Exception occurred while processing print status callback: " + e.getMessage());
        }
    }
}
