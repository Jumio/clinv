/*
 * Jumio Inc.
 *
 * Copyright (C) 2017 - 2018
 * All rights reserved.
 */
package com.jumio.netverify.rest;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jumio.netverify.ConfigurationContext;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.support.BasicAuthorizationInterceptor;
import org.springframework.http.converter.json.GsonHttpMessageConverter;
import org.springframework.util.Assert;
import org.springframework.web.client.RestTemplate;

import static org.apache.http.HttpHeaders.USER_AGENT;

/**
 * Simple facade for building instances of {@link RestTemplate} and supplementary types required for executing an HTTP
 * request.
 */
final public class RestTemplateBuilder {

    private static final String DEFAULT_USER_AGENT = "Jumio Perform NV CLI Tool/v0.1";

    /**
     * Builds a new {@link RestTemplate} instance.
     *
     * @return See above.
     */
    public static RestTemplate buildRestTemplate(ConfigurationContext configuration) {
        Assert.notNull(configuration, "Configuration context cannot be null!");
        RestTemplate restTemplate = new RestTemplate();

        configureRequestInterceptors(configuration, restTemplate);
        configureMessageConverters(restTemplate);

        return restTemplate;
    }

    private static void configureRequestInterceptors(ConfigurationContext configuration, RestTemplate restTemplate) {
        List<ClientHttpRequestInterceptor> interceptors = restTemplate.getInterceptors();
        interceptors.add(new BasicAuthorizationInterceptor(configuration.getApiToken(), configuration.getApiSecret()));
        interceptors.add(new UserAgentHeaderInterceptor(DEFAULT_USER_AGENT));
        restTemplate.setInterceptors(interceptors);
    }

    private static void configureMessageConverters(RestTemplate restTemplate) {
        final Gson gson = new GsonBuilder().disableHtmlEscaping().create();
        restTemplate.getMessageConverters().stream().filter(
                c -> GsonHttpMessageConverter.class.isAssignableFrom(c.getClass())).findFirst().ifPresent(
                c -> ((GsonHttpMessageConverter)c).setGson(gson));
    }

    /**
     * Builds default HTTP headers used with each request.
     *
     * @return See above.
     */
    static HttpHeaders buildHttpHeaders() {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        return httpHeaders;
    }

    private static final class UserAgentHeaderInterceptor implements ClientHttpRequestInterceptor {

        private final String userAgent;

        UserAgentHeaderInterceptor(String userAgent) {
            this.userAgent = userAgent;
        }

        public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution)
                throws IOException {
            request.getHeaders().add(USER_AGENT, this.userAgent);
            return execution.execute(request, body);
        }
    }
}
