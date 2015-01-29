package com.hp.cloudprint.printjobs.controller;

import com.hp.cloudprint.printjobs.config.CoreConfigMock;
import com.hp.cloudprint.printjobs.config.PersistenceConfig;
import com.hp.cloudprint.printjobs.config.PersistenceConfigMock;
import com.hp.cloudprint.printjobs.config.WebConfig;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Created by prabhash on 6/28/2014.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = {CoreConfigMock.class, PersistenceConfigMock.class, WebConfig.class})
public class PrintJobControllerTest {

    @Autowired
    public WebApplicationContext wac;
    private MockMvc mockMvc;

    @Before
    public void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
    }

    @Test
    public void testGetJobs() {
        try {
            this.mockMvc.perform(get("/print/jobs"))
                    .andDo(print())
                    .andExpect(status().isOk());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testPostJob() {
        String requestJson = "{\"name\":\"testjob\",\"type\":\"email\",\"priority\":\"1000\",\"expiry\":\"600\",\"device_id\":\"3498569023\"," +
                "\"source_uri\":\"https://storage.hpeprint.com/emailjobs/123456890/input/file.pdf\"," +
                "\"callback_uri\":\"https://email.hpeprint.com/emailjobs/123456780/callback\"}";
        try {
            this.mockMvc.perform(post("/print/jobs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                    .andDo(print())
                    .andExpect(status().isCreated());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
