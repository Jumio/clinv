/*
 * Jumio Inc.
 *
 * Copyright (C) 2017
 * All rights reserved.
 */
package com.jumio.netverify;

import java.util.Properties;
import org.hamcrest.Matcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;

public class ConfigurationContextIT {

    @Rule
    public final TemporaryFolder tempFolder = new TemporaryFolder();

    @Test
    public void testLoadWithAllValuesSet() throws Exception {
        Properties cliProperties = new Properties();
        cliProperties.setProperty("api.token", "foo");
        cliProperties.setProperty("api.secret", "bar");
        cliProperties.setProperty("api.callbackUrl", "http://example.com");
        cliProperties.setProperty("api.enabledFields", "bizz,buzz");

        ConfigurationContext configuration = new ConfigurationContext();
        configuration.load(Utils.propertiesToList(cliProperties));

        assertThatRequiredPropertiesAre(configuration, notNullValue());
        assertThatOptionalPropertiesAre(configuration, notNullValue());
    }

    @Test
    public void testCtorWithoutLoad() {
        ConfigurationContext configuration = new ConfigurationContext();

        assertThat(configuration, not(nullValue()));
        assertThatRequiredPropertiesAre(configuration, nullValue());
    }

    @Test
    public void testLoadWithoutTokenAndSecret() throws Exception {
        ConfigurationContext configuration = new ConfigurationContext();
        configuration.load();

        assertThatRequiredPropertiesAre(configuration, notNullValue());
        assertThatOptionalPropertiesAre(configuration, nullValue());
    }

    @Test
    public void testLoadMultipleTimesResetsProperties() throws Exception {
        Properties cliProperties = new Properties();
        cliProperties.setProperty("api.token", "foo");

        ConfigurationContext configuration = new ConfigurationContext();
        configuration.load(Utils.propertiesToList(cliProperties));

        assertThat(configuration.getApiToken(), is(notNullValue()));

        configuration.load();

        assertThat(configuration.getApiToken(), is(nullValue()));
    }

    @Test
    public void testLoadWithInvalidCliValues() throws Exception {
        Properties cliProperties = new Properties();
        cliProperties.setProperty("foo.bar", "foo");
        cliProperties.setProperty("api.key=", "invalid");
        cliProperties.setProperty("api..key", "invalid");
        cliProperties.setProperty("api.key==", "invalid");
        cliProperties.setProperty("api.key=foo", "invalid");
        cliProperties.setProperty("api.key=foo=", "invalid");
        cliProperties.setProperty("api=key", "invalid");

        ConfigurationContext configuration = new ConfigurationContext();
        configuration.load(Utils.propertiesToList(cliProperties));

        assertThatRequiredPropertiesAre(configuration, notNullValue());
        assertThatOptionalPropertiesAre(configuration, nullValue());
    }

    private void assertThatRequiredPropertiesAre(ConfigurationContext configuration, Matcher<Object> matcher) {
        assertThat(configuration.getImagesFolder(), is(matcher));
        assertThat(configuration.getImagesFailureFolder(), is(matcher));
        assertThat(configuration.getImagesSuccessFolder(), is(matcher));
        assertThat(configuration.getImagesFrontSuffix(), is(matcher));
        assertThat(configuration.getImagesBackSuffix(), is(matcher));
        assertThat(configuration.getImagesFaceSuffix(), is(matcher));
        assertThat(configuration.getImagesNamePattern(), is(matcher));
        assertThat(configuration.getImagesPresenceStrategy(), is(matcher));
        assertThat(configuration.getApiServerUrl(), is(matcher));
        assertThat(configuration.getApiMerchantReportingCriteria(), is(matcher));
    }

    private void assertThatOptionalPropertiesAre(ConfigurationContext configuration, Matcher<Object> matcher) {
        assertThat(configuration.getApiToken(), is(matcher));
        assertThat(configuration.getApiSecret(), is(matcher));
        assertThat(configuration.getApiCallbackUrl(), is(matcher));
        assertThat(configuration.getApiEnabledFields(), is(matcher));
    }

}