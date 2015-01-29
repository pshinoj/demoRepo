package com.hp.cloudprint.printjobs.queue;

/**
 * Created by prabhash on 7/1/2014.
 */
public class MockJobQueue implements JobQueue {
    @Override
    public Long enqueue(QueueMessage message) {
        return 1L;
    }

    @Override
    public Message dequeue(String queueNames) throws JobQueueException {
        return new Message(1L, new String("1234567890").getBytes());
    }


    @Override
    public void release(Long messageId, Long priority, int delayInSecs) {

    }

    @Override
    public void pause(Long messageId, int timeOutInSecs) {

    }

    @Override
    public void resume(Long messageId) {

    }

    @Override
    public void delete(Long messageId) {

    }

    @Override
    public void close() {

    }
}
