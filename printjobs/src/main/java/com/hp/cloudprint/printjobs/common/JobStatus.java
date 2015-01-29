package com.hp.cloudprint.printjobs.common;

/**
 * Created by prabhash on 6/29/2014.
 */
public class JobStatus {

    public static final String ACCEPTED = "ACCEPTED";
    public static final String PAUSED = "PAUSED";
    public static final String PROCESSING = "PROCESSING";
    public static final String PRINTING = "PRINTING";
    public static final String COMPLETED = "COMPLETED";
    public static final String STOPPED = "STOPPED";
    public static final String FAILED = "FAILED";
    public static final String ABORTED = "ABORTED";
    public static final String RENDER_PENDING = "RENDER_PENDING";
	public static final String RENDER_SUBMIT_SUCCESS = "RENDER_SUBMIT_SUCCESS";
	public static final String RENDER_SUBMIT_FAILED = "RENDER_SUBMIT_FAILED";
	public static final String RENDER_COMPLETE = "RENDER_COMPLETE";
	public static final String PRINT_PENDING = "PRINT_PENDING";
	public static final String PRINT_SUBMIT_SUCCESS = "PRINT_SUBMIT_SUCCESS";
	public static final String PRINT_SUBMIT_FAILED = "PRINT_SUBMIT_FAILED";
	public static final String PRINT_COMPLETE = "PRINT_COMPLETE";
	public static final String VALIDATE_PENDING = "VALIDATE_PENDING";


    private String status;
    private Integer statusCode;
    private String statusMessage;

    public JobStatus(String status) {
        buildJobStatus(status);
    }

    public JobStatus(String status, Integer errorCode, String statusMessage) {
        this.status = status;
        this.statusCode = errorCode;
        this.statusMessage = statusMessage;
    }

    private void buildJobStatus(String status) {
        switch (status) {
            case COMPLETED:
            {
                this.status = COMPLETED;
                this.statusCode = 0;
                this.statusMessage = "Job processed successfully";
                break;
            }

            case ACCEPTED:
            {
                this.status = ACCEPTED;
                this.statusCode = 1;
                this.statusMessage = "Job is accepted and waiting to get processed";
                break;
            }

            case PROCESSING:
            {
                this.status = PROCESSING;
                this.statusCode = 2;
                this.statusMessage = "Job is getting processed to send to the printer";
                break;
            }

            case PRINTING:
            {
                this.status = PRINTING;
                this.statusCode = 3;
                this.statusMessage = "Printer is printing the job";
                break;
            }

            case STOPPED:
            {
                this.status = STOPPED;
                this.statusCode = 4;
                this.statusMessage = "Job is held since the printer is waiting for user intervention";
                break;
            }

            case FAILED:
            {
                this.status = FAILED;
                this.statusCode = 5;
                this.statusMessage = "Job processing failed due to service error";
                break;
            }
			
			case RENDER_PENDING:
            {
            	this.status = RENDER_PENDING;
                this.statusCode = 5;
                this.statusMessage = "Job is waiting to be submitted to rendering service";
            }
            
            case RENDER_SUBMIT_SUCCESS:
            {
            	this.status = RENDER_SUBMIT_SUCCESS;
                this.statusCode = 6;
                this.statusMessage = "Job is submitted to rendering service";
            }
            
            case RENDER_SUBMIT_FAILED:
            {
            	this.status = RENDER_SUBMIT_FAILED;
                this.statusCode = 6;
                this.statusMessage = "Job submission to rendering service failed";
            }
            
            case RENDER_COMPLETE:
            {
            	this.status = RENDER_COMPLETE;
                this.statusCode = 6;
                this.statusMessage = "Rendering service completed the job";
            }
            
            case PRINT_PENDING:
            {
            	this.status = PRINT_PENDING;
                this.statusCode = 7;
                this.statusMessage = "Job is waiting to be submitted to print service";
            }
            
            case PRINT_SUBMIT_SUCCESS:
            {
            	this.status = PRINT_SUBMIT_SUCCESS;
                this.statusCode = 8;
                this.statusMessage = "Job is submitted to print service";
            }
            
            case PRINT_SUBMIT_FAILED:
            {
            	this.status = PRINT_SUBMIT_FAILED;
                this.statusCode = 9;
                this.statusMessage = "Job submission to print service failed";
            }
            
            case PRINT_COMPLETE:
            {
            	this.status = PRINT_COMPLETE;
                this.statusCode = 10;
                this.statusMessage = "Print service completed the job";
            }

            case ABORTED:
            {
                this.status = ABORTED;
                this.statusCode = 6;
                this.statusMessage = "Job is aborted either by user or printer";
                break;
            }

            default:
            {
                this.status = status;
            }
        }
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatusMessage() {
        return statusMessage;
    }

    public void setStatusMessage(String statusMessage) {
        this.statusMessage = statusMessage;
    }

    public Integer getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(Integer statusCode) {
        this.statusCode = statusCode;
    }
}
