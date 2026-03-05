package com.postal_fetcher_service.postal_fetcher_service.config;

import jakarta.annotation.PostConstruct;
import javax.net.ssl.*;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@Configuration
public class JdkTrustAllConfig {

    /**
     * DEV-ONLY: Trust all SSL certs globally for HttpsURLConnection.
     * DO NOT USE IN PRODUCTION.
     */
    @PostConstruct
    public void trustAllHttps() throws Exception {
        TrustManager[] trustAll = new TrustManager[]{
                new X509TrustManager() {
                    public void checkClientTrusted(X509Certificate[] chain, String authType) { }
                    public void checkServerTrusted(X509Certificate[] chain, String authType) { }
                    public X509Certificate[] getAcceptedIssuers() { return new X509Certificate[0]; }
                }
        };
        SSLContext sc = SSLContext.getInstance("TLS");
        sc.init(null, trustAll, new SecureRandom());
        HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        HttpsURLConnection.setDefaultHostnameVerifier((hostname, session) -> true);
    }

    @Bean
    public RestTemplate restTemplate() {
        // Default RestTemplate backed by JDK HttpURLConnection
        SimpleClientHttpRequestFactory f = new SimpleClientHttpRequestFactory();
        f.setConnectTimeout(5000);
        f.setReadTimeout(5000);
        return new RestTemplate(f);
    }
}
