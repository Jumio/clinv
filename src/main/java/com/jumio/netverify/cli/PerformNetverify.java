/*
 * Jumio Inc.
 *
 * Copyright (C) 2017
 * All rights reserved.
 */
package com.jumio.netverify.cli;

import com.jumio.netverify.ConfigurationContext;
import com.jumio.netverify.domain.ImageTriplet;
import com.jumio.netverify.domain.ImageTripletRepository;
import com.jumio.netverify.rest.NetverifyApiFacade;
import com.jumio.netverify.rest.RestTemplateBuilder;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

/**
 * Main entry point for the CLI tool.
 */
final class PerformNetverify {

    private static final Logger logger = LoggerFactory.getLogger(PerformNetverify.class);

    public static void main(String[] args) {
        // Load the configuration
        ConfigurationContext configuration = loadConfiguration(Arrays.asList(args), new ConfigurationContext());
        verifyApiCredentialsPresent(configuration);

        // Create destination folders
        ImageTripletRepository repository = new ImageTripletRepository(configuration);
        createRequiredDirectories(repository);

        // Find image triplets in the image folder
        List<ImageTriplet> imageTriplets = loadImages(repository);

        // Send requests to the API
        RestTemplate restTemplate = RestTemplateBuilder.buildRestTemplate(configuration);
        new NetverifyApiFacade(configuration, repository, restTemplate).sendApiRequests(imageTriplets);
    }

    /**
     * Loads the application configuration context and tries to merge the CLI property values with the configuration
     * file properties. The returned object is used to set up API interactions.
     *
     * @param cliArguments A list of property values provided from the CLI.
     * @param configurationContext An empty configuration context.
     * @return A fully loaded configuration context. In case of an unrecoverable error, the application exits.
     */
    static ConfigurationContext loadConfiguration(List<String> cliArguments,
            ConfigurationContext configurationContext) {
        try {
            configurationContext.load(cliArguments);
        } catch (IOException ioe) {
            logger.error("Error loading {}", ioe.getMessage());
            System.exit(255);
        }
        return configurationContext;
    }

    /**
     * Checks if the API token and secret are present in the configuration context. In case no API credentials have been
     * provided, the application exits.
     *
     * @param configuration The configuration context being inspected.
     */
    private static void verifyApiCredentialsPresent(ConfigurationContext configuration) {
        if (StringUtils.isEmpty(configuration.getApiToken()) || StringUtils.isEmpty(configuration.getApiSecret())) {
            logger.error("Missing API credentials.");
            System.exit(255);
        }
    }

    /**
     * Creates directories (e.g. for failed API request) required during the lifetime of the CLI tool. In case of an I/O
     * error, the application exits.
     *
     * @param repository The API object responsible for interacting with the underlying storage.
     */
    static void createRequiredDirectories(ImageTripletRepository repository) {
        try {
            repository.createDirectories();
        } catch (IOException ioe) {
            logger.error("Error creating required directories: {}", ioe.getMessage());
            System.exit(255);
        }
    }

    /**
     * Tries to open the required images folder. In case of an I/O error, the application exits.
     *
     * @param repository The image repository that loads the images.
     * @return A collection of image triplets from the images folder.
     */
    static List<ImageTriplet> loadImages(ImageTripletRepository repository) {
        try {
            return repository.findAll();
        } catch (IOException ioe) {
            logger.error("Error opening {}", ioe.getMessage());
            System.exit(255);
        }
        return Collections.emptyList();
    }
}
