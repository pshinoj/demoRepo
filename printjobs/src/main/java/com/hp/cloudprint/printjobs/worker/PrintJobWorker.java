package com.hp.cloudprint.printjobs.worker;

import com.hp.cloudprint.printjobs.jobevent.EventHandler;
import com.hp.cloudprint.printjobs.workflow.PrintFlow;
import com.hp.cloudprint.printjobs.workflow.RenderFlow;
import com.hp.cloudprint.printjobs.workflow.ValidateFlow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/**
 * Created by prabhash on 9/9/2014.
 */
public class PrintJobWorker {
    private static final Logger LOG = LoggerFactory.getLogger(PrintJobWorker.class);

    @Autowired
    ThreadPoolTaskExecutor taskExecutor;

    @Autowired
    ValidateFlow validateFlow;
    @Autowired
    RenderFlow renderFlow;
    @Autowired
    PrintFlow printFlow;
    @Autowired
    EventHandler eventHandler;

    public void start() {
        LOG.info("Starting worker with workflow thread pools : validate, render, print");
        startEventHandler();
        startValidateWorkflow();
        startRenderWorkflow();
        startPrintWorkflow();
    }

    private void startEventHandler() {
        int startPool = 1;
        for (int count = 0; count < startPool; count++) {
            taskExecutor.execute(new Runnable() {
                @Override
                public void run() {
                    eventHandler.start();
                }
            });
        }
    }

    private void startPrintWorkflow() {
        int startPool = 1;
        for (int count = 0; count < startPool; count++) {
            taskExecutor.execute(new Runnable() {
                @Override
                public void run() {
                    printFlow.start();
                }
            });
        }
    }

    private void startRenderWorkflow() {
        int startPool = 1;
        for (int count = 0; count < startPool; count++) {
            taskExecutor.execute(new Runnable() {
                @Override
                public void run() {
                    renderFlow.start();
                }
            });
        }
    }

    private void startValidateWorkflow() {
        int startPool = 1;
        for (int count = 0; count < startPool; count++) {
            taskExecutor.execute(new Runnable() {
                @Override
                public void run() {
                    validateFlow.start();
                }
            });
        }
    }
}
