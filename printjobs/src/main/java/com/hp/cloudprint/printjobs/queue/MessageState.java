package com.hp.cloudprint.printjobs.queue;

/**
 * Created by prabhash on 7/1/2014.
 */
public enum MessageState {
    READY,
    RESERVED,
    DELAYED,
    PAUSED,
    EXPIRED
}
