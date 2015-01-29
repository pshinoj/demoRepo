package com.hp.cloudprint.printjobs.queue;

/**
 * Created by prabhash on 7/1/2014.
 */
public interface JobQueue {
    public Long enqueue(QueueMessage message) throws JobQueueException;
    public Message dequeue(String queueNames) throws JobQueueException;
    public void release(Long messageId, Long priority, int delayInSecs);
    public void pause(Long messageId, int timeOutInSecs);
    public void resume(Long messageId);
    public void delete(Long messageId);
    public void close();
}
