package com.hp.cloudprint.printjobs.queue;

/**
 * Created by prabhash on 10/8/2014.
 */
public class JobQueueException extends Throwable {

    private String message;

    public JobQueueException(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
