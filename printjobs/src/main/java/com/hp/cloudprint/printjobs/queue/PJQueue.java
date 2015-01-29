package com.hp.cloudprint.printjobs.queue;

/**
 * Created by prabhash on 9/22/2014.
 */
public enum PJQueue {
    SUBMIT_QUEUE("job-queue"),
    RENDER_QUEUE("render-queue"),
    PRINT_QUEUE("print-queue"),
    EVENT_QUEUE("event-queue");

    private final String name;

    PJQueue(String s) {
        this.name = s;
    }

    public boolean equalsName(String otherName){
        return (otherName == null)? false:name.equals(otherName);
    }

    public String toString(){
        return name;
    }
}
