package com.hp.cloudprint.printjobs.workflow.render;



import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by prabhash on 7/10/2014.
 */

public class RenderRequest {

    @JsonProperty(value = "ref_id")
    private String refId;
    private Map<String, String> callback;
    private Integer priority;
    private Map<String, List<InputMeta>> inputs;
    @JsonIgnore
    private List<InputMeta> inputMetaList;
    @JsonProperty(value = "output_mime_type")
    private String outputMime;
    @JsonProperty(value = "settings")
    private List<Map<String, String>> settingsList;
    @JsonIgnore
    private Map<String, String> settings;

    public String getRefId() {
        return refId;
    }

    public void setRefId(String refId) {
        this.refId = refId;
    }

    public Map<String, String> getCallback() {
        return callback;
    }

    public void addCallback(String callbackUri) {
        if (this.callback == null) {
            this.callback = new HashMap<String, String>();
        }
        this.callback.put("url", callbackUri);
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public Map<String, List<InputMeta>> getInputs() {
        return inputs;
    }

    public void setInputs(Map<String, List<InputMeta>> inputs) {
        this.inputs = inputs;
        this.inputMetaList = inputs.get("pointers");
    }

    public List<InputMeta> getInputMetaList() {
        return inputMetaList;
    }

    public void addInputMeta(InputMeta input) {
        if (inputMetaList == null) {
            inputMetaList = new ArrayList<InputMeta>();
        }
        inputMetaList.add(input);
        if (inputs == null) {
            this.inputs = new HashMap<String, List<InputMeta>>();
        }
        if (this.inputs.containsKey("pointers")) {
            this.inputs.clear();
        }
        this.inputs.put("pointers", inputMetaList);
    }

    public String getOutputMime() {
        return outputMime;
    }

    public void setOutputMime(String outputMime) {
        this.outputMime = outputMime;
    }

    public List<Map<String, String>> getSettingsList() {
        return settingsList;
    }

    public void setSettingsList(List<Map<String, String>> settingsList) {
        this.settingsList = settingsList;
    }

    public Map<String, String> getSettings() {
        return settings;
    }

    public void setSettings(Map<String, String> settings) {
        if (settings != null) {
            this.settings = settings;
            this.settingsList = new ArrayList<>();
            for (String key : settings.keySet()) {
                Map<String, String> setting = new HashMap<String, String>();
                setting.put("name", key);
                setting.put("value", settings.get(key));
                this.settingsList.add(setting);
            }
        }
    }
}
