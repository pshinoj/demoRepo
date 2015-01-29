package com.hp.cloudprint.printjobs.common;

/**
 * Created by prabhash on 8/20/2014.
 */
public enum JobStatusInternal {
    VALIDATING,
    VALIDATE_SUCCESS,
    VALIDATE_FAILED,
    RENDER_SUBMITTED,
    RENDER_PROCESSING,
    RENDER_PENDING,
    RENDER_FAILED,
    RENDER_SUCCESS,
    PRINT_SUBMITTED,
    PRINT_PROCESSING,
    PRINT_PENDING,
    PRINT_STOPPED,
    PRINT_FAILED,
    PRINT_SUCCESS,
    ABORTED
}
