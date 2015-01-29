package com.hp.cloudprint.printjobs.workflow.render;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.map.ser.std.DateSerializer;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by prabhash on 7/10/2014.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class RenderJob {
    private String id;
    @JsonProperty("ref_id")
    private String refId;
    private String status;
    private String started;
    private String expires;
    private String finished;
    private Boolean evicted;
    private List<Map<String,String>> inputs;
    @JsonIgnore
    private Map<String, List<InputMeta>> tempInputs;
    private List<Map<String, String>> settings;
    private List<Map<String, String>> links;
    @JsonIgnore
    private Map<String,String> relLinks;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRefId() {
        return refId;
    }

    public void setRefId(String refId) {
        this.refId = refId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStarted() {
        return started;
    }

    public void setStarted(String started) {
        this.started = started;
    }

    public String getExpires() {
        return expires;
    }

    public void setExpires(String expires) {
        this.expires = expires;
    }

    public String getFinished() {
        return finished;
    }

    public void setFinished(String finished) {
        this.finished = finished;
    }

    public Boolean getEvicted() {
        return evicted;
    }

    public void setEvicted(Boolean evicted) {
        this.evicted = evicted;
    }

    public Map<String, List<InputMeta>> getTempInputs() {
        return tempInputs;
    }

    public void setTempInputs(Map<String, List<InputMeta>> tempInputs) {
        this.tempInputs = tempInputs;
    }

    public List<Map<String, String>> getSettings() {
        return settings;
    }

    public void setSettings(List<Map<String, String>> settings) {
        this.settings = settings;
    }

    public List<Map<String, String>> getLinks() {
        return links;
    }

    public void setLinks(List<Map<String, String>> links) {
        this.links = links;
    }

    public List<Map<String, String>> getInputs() {
        return inputs;
    }

    public void setInputs(List<Map<String, String>> inputs) {
        this.inputs = inputs;
    }

    public Map<String, String> getRelLinks() {
        if (relLinks == null || relLinks.size() == 0) {
            relLinks = new HashMap<String, String>();
            if (links != null) {
                for(Map<String, String> item : links) {
                    relLinks.put(item.get("rel"), item.get("href"));
                }
            }
        }
        return relLinks;
    }
}
