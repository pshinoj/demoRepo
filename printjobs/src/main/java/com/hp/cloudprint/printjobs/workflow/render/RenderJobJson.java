package com.hp.cloudprint.printjobs.workflow.render;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by prabhash on 7/17/2014.
 */
public class RenderJobJson {

    private String id;
    @JsonProperty(value = "ref_id")
    private String refId;
    private String status;
    private String created;
    private String started;
    private String expires;
    private String finished;
    private Boolean evicted;
    private List<Map<String, String>> inputs;
    private List<Map<String, String>> links;
    @JsonIgnore
    private Map<String, String> relLinks;

    public String getStarted() {
        return started;
    }

    public void setStarted(String started) {
        this.started = started;
    }

    public void setExpires(String expires) {
        this.expires = expires;
    }

    public void setFinished(String finished) {
        this.finished = finished;
    }

    public List<Map<String, String>> getInputs() {
        return inputs;
    }

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

    @JsonIgnore
    public void setStartedAt(Date started) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        this.started = dateFormat.format(started);
    }

    public String getExpires() {
        return expires;
    }

    @JsonIgnore
    public void setExpiresAt(Date expires) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        this.expires = dateFormat.format(expires);
    }

    public String getFinished() {
        return finished;
    }

    @JsonIgnore
    public void setFinishedAt(Date finished) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        this.finished = dateFormat.format(finished);
    }

    public Boolean getEvicted() {
        return evicted;
    }

    public void setEvicted(Boolean evicted) {
        this.evicted = evicted;
    }

    public void setInputs(List<Map<String, String>> inputs) {
        this.inputs = inputs;
    }

    public List<Map<String, String>> getLinks() {
        return links;
    }

    public void setLinks(List<Map<String, String>> links) {
        this.links = links;
    }

    public void addInput(InputMeta inputMeta) {
        if (inputMeta == null) {
            return;
        }

        if (this.inputs == null) {
            this.inputs = new ArrayList<Map<String,String>>();
        }
        Map<String, String> item = new HashMap<String, String>();
        item.put("url", inputMeta.getSourceUri());
        item.put("content_type", inputMeta.getContentType());
        this.inputs.add(item);
    }

    public void addRelLink(String rel, String href) {
        if (this.links == null) {
            this.links = new ArrayList<Map<String,String>>();
        }
        if (relLinks == null) {
            this.relLinks = new HashMap<String, String>();
        }
        this.relLinks.put(rel, href);
        Map<String, String> item = new HashMap<String, String>();
        item.put("rel", rel);
        item.put("href", href);
        this.links.add(item);
    }

    public String getRelLink(String rel) {
        String val = null;
        this.relLinks = getRelLinks();
        if (relLinks.containsKey(rel)) {
            return relLinks.get(rel);
        }
        return val;
    }

    public Map<String, String> getRelLinks() {
        if (relLinks == null && links != null) {
            this.relLinks = new HashMap<String, String>();
            for (Map<String, String> item : links) {
                this.relLinks.put(item.get("rel"), item.get("href"));
            }
        }
        return relLinks;
    }

    public void setRelLinks(Map<String, String> relLinks) {
        this.relLinks = relLinks;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }
}
