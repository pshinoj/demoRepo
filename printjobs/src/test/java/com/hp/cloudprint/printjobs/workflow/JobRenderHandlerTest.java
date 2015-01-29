package com.hp.cloudprint.printjobs.workflow;

import com.hp.cloudprint.printjobs.common.HttpClientHelper;
import com.hp.cloudprint.printjobs.workflow.render.RenderServiceUtil;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.junit.Test;

import javax.net.ssl.HttpsURLConnection;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by prabhash on 7/30/2014.
 */
public class JobRenderHandlerTest {
    private static final RenderServiceUtil serviceUtil = new RenderServiceUtil();

    //@Test
    public void testSierraUsingApacheClient() {
        HttpClient client  = HttpClientHelper.createClient();
        HttpGet httpGet = new HttpGet("https://15.125.37.154/rendering-api/v1/job_types/");
        httpGet.addHeader(org.apache.http.HttpHeaders.AUTHORIZATION, serviceUtil.authToken());
        try {
            HttpResponse response = client.execute(httpGet);
            String responseJson = getStringFromInputStream(response.getEntity().getContent());
            System.out.print(responseJson);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // convert InputStream to String
    private static String getStringFromInputStream(InputStream is) {

        BufferedReader br = null;
        StringBuilder sb = new StringBuilder();

        String line;
        try {

            br = new BufferedReader(new InputStreamReader(is));
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return sb.toString();

    }
}
