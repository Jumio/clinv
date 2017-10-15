/*
 * Jumio Inc.
 *
 * Copyright (C) 2017
 * All rights reserved.
 */
package com.jumio.netverify.rest;

import com.jumio.netverify.ConfigurationContext;
import com.jumio.netverify.Utils;
import java.util.Properties;
import org.junit.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.web.client.RestTemplate;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;

public class RestTemplateBuilderTest {

    @Test
    public void testBuildRestTemplate() throws Exception {
        ConfigurationContext configuration = new ConfigurationContext();
        Properties cliProperties = new Properties();
        cliProperties.put("api.token", "foo");
        cliProperties.put("api.secret", "bar");
        configuration.load(Utils.propertiesToList(cliProperties));
        RestTemplate restTemplate = RestTemplateBuilder.buildRestTemplate(configuration);

        assertThat(restTemplate, not(nullValue()));
        assertThat(restTemplate.getInterceptors().size(), is(2));
    }

    @Test
    public void testBuildHttpHeaders() {
        HttpHeaders httpHeaders = RestTemplateBuilder.buildHttpHeaders();

        assertThat(httpHeaders, not(nullValue()));
    }
}