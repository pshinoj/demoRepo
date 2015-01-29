package com.hp.cloudprint.printjobs.queue;

/**
 * Created by prabhash on 7/1/2014.
 */
public class Message {
    private Long id;
    private byte[] data;

    public Message(Long id, byte[] data) {
        this.id = id;
        this.data = data;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }
}
