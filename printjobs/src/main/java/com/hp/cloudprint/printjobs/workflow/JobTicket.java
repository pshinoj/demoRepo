package com.hp.cloudprint.printjobs.workflow;

import java.io.Serializable;

/**
 * Created by prabhash on 9/22/2014.
 */
public class JobTicket implements Serializable {
    private String mediaSize;
    private String mediaType;
    private String deviceFamily;
    private String deviceModel;
    private String printFormat;
    private String orientation;
    private String inputBin;
    private String quality;
    private String plexMode;

    public String getMediaSize() {
        return mediaSize;
    }

    public void setMediaSize(String mediaSize) {
        this.mediaSize = mediaSize;
    }

    public String getMediaType() {
        return mediaType;
    }

    public void setMediaType(String mediaType) {
        this.mediaType = mediaType;
    }

    public String getDeviceFamily() {
        return deviceFamily;
    }

    public void setDeviceFamily(String deviceFamily) {
        this.deviceFamily = deviceFamily;
    }

    public String getDeviceModel() {
        return deviceModel;
    }

    public void setDeviceModel(String deviceModel) {
        this.deviceModel = deviceModel;
    }

    public String getPrintFormat() {
        return printFormat;
    }

    public void setPrintFormat(String printFormat) {
        this.printFormat = printFormat;
    }

    public String getOrientation() {
        return orientation;
    }

    public void setOrientation(String orientation) {
        this.orientation = orientation;
    }

    public String getInputBin() {
        return inputBin;
    }

    public void setInputBin(String inputBin) {
        this.inputBin = inputBin;
    }

    public String getQuality() {
        return quality;
    }

    public void setQuality(String quality) {
        this.quality = quality;
    }

    public String getPlexMode() {
        return plexMode;
    }

    public void setPlexMode(String plexMode) {
        this.plexMode = plexMode;
    }
}
