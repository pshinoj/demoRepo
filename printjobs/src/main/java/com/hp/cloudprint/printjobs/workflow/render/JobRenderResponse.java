package com.hp.cloudprint.printjobs.workflow.render;

import java.util.List;
import java.util.Map;

/**
 * Created by prabhash on 7/16/2014.
 */
public class JobRenderResponse {
    RenderJobJson job;
    private List<OutputMeta> outputs;
    private List<Map<String, String>> settings;

    public List<OutputMeta> getOutputs() {
        return outputs;
    }

    public void setOutputs(List<OutputMeta> outputs) {
        this.outputs = outputs;
    }

    public List<Map<String, String>> getSettings() {
        return settings;
    }

    public void setSettings(List<Map<String, String>> settings) {
        this.settings = settings;
    }

    public RenderJobJson getJob() {
        return job;
    }

    public void setJob(RenderJobJson job) {
        this.job = job;
    }
}
