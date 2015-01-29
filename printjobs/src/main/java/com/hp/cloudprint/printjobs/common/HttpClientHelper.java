package com.hp.cloudprint.printjobs.common;

import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContexts;
import org.apache.http.conn.ssl.X509HostnameVerifier;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.DefaultProxyRoutePlanner;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.ClientProperties;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.*;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.Configuration;

public class HttpClientHelper {
    private static final AppConfig appConfig = new AppConfig();

    private static final TrustManager[ ] certs = new TrustManager[ ] {
            new X509TrustManager() {
                @Override
                public X509Certificate[] getAcceptedIssuers() {
                    return null;
                }
                @Override
                public void checkServerTrusted(X509Certificate[] chain, String authType)
                        throws CertificateException {
                }
                @Override
                public void checkClientTrusted(X509Certificate[] chain, String authType)
                        throws CertificateException {
                }
            }
    };

    public static HttpClient createClient(Boolean useProxy) {
        PlainConnectionSocketFactory plainSF = PlainConnectionSocketFactory.getSocketFactory();
        SSLContext ctx = null;
        try {
            ctx = SSLContext.getInstance("SSL");
            ctx.init(null, certs, new SecureRandom());
        } catch (java.security.GeneralSecurityException ex) {
        }
        SSLConnectionSocketFactory sslSF = new SSLConnectionSocketFactory(ctx, new X509HostnameVerifier() {
            @Override
            public void verify(String s, SSLSocket sslSocket) throws IOException {

            }

            @Override
            public void verify(String s, X509Certificate x509Certificate) throws SSLException {

            }

            @Override
            public void verify(String s, String[] strings, String[] strings2) throws SSLException {

            }

            @Override
            public boolean verify(String s, SSLSession sslSession) {
                return false;
            }
        });
        Registry<ConnectionSocketFactory> registry = RegistryBuilder.<ConnectionSocketFactory>create()
                .register("http", plainSF)
                .register("https", sslSF)
                .build();

        HttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager(registry);
        if (useProxy) {
            HttpHost proxy = new HttpHost(appConfig.proxyHost(), appConfig.proxyPort());
            DefaultProxyRoutePlanner routePlanner = new DefaultProxyRoutePlanner(proxy);
            return HttpClients.custom().setConnectionManager(connectionManager).setRoutePlanner(routePlanner).build();
        } else {
            return HttpClients.custom().setConnectionManager(connectionManager).build();
        }
    }

    public static HttpClient createClient() {
        return createClient(appConfig.proxyEnabled());
    }

    public static String convertHttpResponseToString(HttpResponse response) {
        if (response == null) {
            return null;
        }

        BufferedReader br = null;
        StringBuilder sb = new StringBuilder();
        InputStream is = null;
        try {
            is = response.getEntity().getContent();
        } catch (IOException e) {
            e.printStackTrace();
        }

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