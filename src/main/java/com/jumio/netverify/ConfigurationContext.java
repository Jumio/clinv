/*
 * Jumio Inc.
 *
 * Copyright (C) 2017
 * All rights reserved.
 */
package com.jumio.netverify;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.StringUtils;

/**
 * Holds all properties relevant for the CLI tool.
 * <p>
 * The user can provide key-value pairs on the CLI that override the defualts. Externalized properties are also
 * supported - they have to be located in the same directory as the JAR file.
 */
public final class ConfigurationContext {

    private static final Logger logger = LoggerFactory.getLogger(ConfigurationContext.class);

    public static final String JPEG_IMAGE = ".jpg";

    public static final String PNG_IMAGE = ".png";

    private static final String DEFAULT_PROPERTIES_FILE = "config.properties";

    private static final String API_CALLBACK_URL = "api.callbackUrl";

    private static final String API_ENABLED_FIELDS = "api.enabledFields";

    private static final String API_MERCHANT_REPORTING_CRITERIA = "api.merchantReportingCriteria";

    private static final String API_SECRET = "api.secret";

    private static final String API_SERVER_URL = "api.serverUrl";

    private static final String API_TOKEN = "api.token";

    private static final String IMAGES_BACK_SUFFIX = "images.backSuffix";

    private static final String IMAGES_FACE_SUFFIX = "images.faceSuffix";

    private static final String IMAGES_FAILURE_FOLDER = "images.failureFolder";

    private static final String IMAGES_FOLDER = "images.folder";

    private static final String IMAGES_FRONT_SUFFIX = "images.frontSuffix";

    private static final String IMAGES_NAME_PATTERN = "images.namePattern";

    private static final String IMAGES_PRESENCE_STRATEGY = "images.presenceStrategy";

    private static final String IMAGES_SUCCESS_FOLDER = "images.successFolder";

    private Properties properties;

    public ConfigurationContext() {
        properties = new Properties();
    }

    /**
     * Loads the properties from a file into memory, overriding entries with the provided collection. This method
     * destroys the old, if any, state and creates a new object each time it is called.
     *
     * @param overridingProperties A collection of properties that should override the entries from the file.
     * @return A loaded configuration context object.
     */
    public ConfigurationContext load(List<String> overridingProperties) throws IOException {
        return load(overridingProperties, DEFAULT_PROPERTIES_FILE);
    }

    public ConfigurationContext load() throws IOException {
        return load(Collections.emptyList(), DEFAULT_PROPERTIES_FILE);
    }

    private ConfigurationContext load(List<String> overridingProperties, String propertiesFileName) throws IOException {
        if (!properties.isEmpty()) {
            properties = new Properties();
        }
        try (InputStream externalProperties = new FileInputStream(propertiesFileName)) {
            properties.load(externalProperties);
            logger.info("Loaded external configuration from {}.", propertiesFileName);
        } catch (FileNotFoundException fnfe) {
            InputStream internalProperties = new ClassPathResource(propertiesFileName).getInputStream();
            properties.load(internalProperties);
            logger.info("No external configuration provided. Loaded internal defaults.");
            internalProperties.close();
        }

        mergeProperties(overridingProperties);
        return this;
    }

    private void mergeProperties(List<String> cliArguments) {
        cliArguments.stream()
                .map(s -> s.split("="))
                .filter(array -> (properties.containsKey(array[0]) || API_TOKEN.equals(array[0]) ||
                        API_SECRET.equals(array[0])))
                .forEach(array -> {
                    String value = array.length == 1 ? "" : array[1];
                    logger.info("Overriding setting: {} with value: {}", array[0], value);
                    properties.put(array[0], value);
                });
    }

    public String getImagesFolder() {
        return nullIfNoText(properties.getProperty(IMAGES_FOLDER));
    }

    public String getImagesFailureFolder() {
        return nullIfNoText(properties.getProperty(IMAGES_FAILURE_FOLDER));
    }

    public String getImagesSuccessFolder() {
        return nullIfNoText(properties.getProperty(IMAGES_SUCCESS_FOLDER));
    }

    public String getImagesFrontSuffix() {
        return nullIfNoText(properties.getProperty(IMAGES_FRONT_SUFFIX));
    }

    public String getImagesBackSuffix() {
        return nullIfNoText(properties.getProperty(IMAGES_BACK_SUFFIX));
    }

    public String getImagesFaceSuffix() {
        return nullIfNoText(properties.getProperty(IMAGES_FACE_SUFFIX));
    }

    public String getImagesNamePattern() {
        return nullIfNoText(properties.getProperty(IMAGES_NAME_PATTERN));
    }

    public String getImagesPresenceStrategy() {
        return nullIfNoText(properties.getProperty(IMAGES_PRESENCE_STRATEGY));
    }

    public String getApiCallbackUrl() {
        return nullIfNoText(properties.getProperty(API_CALLBACK_URL));
    }

    public String getApiEnabledFields() {
        return nullIfNoText(properties.getProperty(API_ENABLED_FIELDS));
    }

    public String getApiServerUrl() {
        return nullIfNoText(properties.getProperty(API_SERVER_URL));
    }

    public String getApiMerchantReportingCriteria() {
        return nullIfNoText(properties.getProperty(API_MERCHANT_REPORTING_CRITERIA));
    }

    public String getApiToken() {
        return nullIfNoText(properties.getProperty(API_TOKEN));
    }

    public String getApiSecret() {
        return nullIfNoText(properties.getProperty(API_SECRET));
    }

    private String nullIfNoText(String configurationValue) {
        return StringUtils.hasText(configurationValue) ? configurationValue : null;
    }
}
