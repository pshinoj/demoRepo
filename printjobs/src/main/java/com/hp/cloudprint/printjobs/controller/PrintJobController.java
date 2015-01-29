package com.hp.cloudprint.printjobs.controller;

import com.hp.cloudprint.printjobs.common.*;
import com.hp.cloudprint.printjobs.model.Job;
import com.hp.cloudprint.printjobs.service.PrintJobService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;


/**
 * Created by prabhash on 6/26/2014.
 */

@RestController
@RequestMapping(value = "/print")
public class PrintJobController {
    private static final Logger LOG = LoggerFactory.getLogger(PrintJobController.class);
    private static final AppConfig appConfig = new AppConfig();
    @Autowired
    PrintJobService jobService;

    @RequestMapping(value = "/jobs", method = RequestMethod.GET)
    public @ResponseBody JobResponse getIt() {
        JobResponse response = new JobResponse();
        response.setJobId("123");
        return response;
    }

    @RequestMapping(value = "/jobs", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public @ResponseBody JobResponse enqueueJob(@RequestBody JobRequest jobRequest) {
        final Job job = buildJobModel(jobRequest);
        PrintJob printJob = jobService.submitJob(job);
        return buildJobResponse(printJob);
    }

    private JobResponse buildJobResponse(PrintJob printJob) {
        JobResponse response = new JobResponse();
        response.setJobId(printJob.getPrintJobId());
        JobStatus status = new JobStatus(printJob.getStatus());
        status.setStatusMessage(printJob.getStatusMessage());
        response.setJobStatus(status);
        String selfUri = appConfig.getSelfUri();
        String jobUri = selfUri+"/jobs/"+printJob.getPrintJobId();
        response.addRelLink("self", jobUri);
        return response;
    }

    @RequestMapping(value = "/jobs/{printJobId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public @ResponseBody JobResponse getJobStatus(@PathVariable String printJobId) {
        JobStatus status = jobService.getJobStatus(printJobId);
        JobResponse response = new JobResponse();
        response.setJobId(printJobId);
        response.setJobStatus(status);
        return response;
    }

    private Job buildJobModel(JobRequest jobRequest) {
        Job job = new Job();
        job.setStatus(JobStatus.ACCEPTED);
        job.setName(jobRequest.getName());
        job.setType(jobRequest.getType());
        LocalDateTime ldt = LocalDateTime.now().plusSeconds(jobRequest.getExpiry());
        Date expire = Date.from(ldt.atZone(ZoneId.systemDefault()).toInstant());
        job.setExpiresAt(expire);
        // TODO: Create a set of Enum values easily usable by clients and restrict some of the internal high priority numbers
        // TODO: to use it internally only
        job.setPriority(jobRequest.getPriority());
        // TODO: Create a data model to map deviceUri (String) to deviceId (Long) value
        job.setDeviceId(jobRequest.getDeviceId());
        job.setCallbackUri(jobRequest.getCallbackUri());
        job.setSourceUri(jobRequest.getSourceUri());
        job.setContentType(jobRequest.getContentType());
        job.setSettings(jobRequest.getSettings());
        return job;
    }

    private Long fetchDeviceId(String deviceId) {
        return 3498569023L;
    }


}
