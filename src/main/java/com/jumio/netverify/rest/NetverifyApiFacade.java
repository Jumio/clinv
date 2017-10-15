/*
 * Jumio Inc.
 *
 * Copyright (C) 2017
 * All rights reserved.
 */
package com.jumio.netverify.rest;

import com.jumio.netverify.ConfigurationContext;
import com.jumio.netverify.domain.ImageTriplet;
import com.jumio.netverify.domain.ImageTripletRepository;
import com.jumio.netverify.domain.PresenceStrategy;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.util.Assert;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

/**
 * Facade for the perform Netverify endpoint.
 */
public final class NetverifyApiFacade {

    private static final Logger logger = LoggerFactory.getLogger(NetverifyApiFacade.class);

    private final RestTemplate restTemplate;

    private final HttpHeaders httpHeaders;

    private final ConfigurationContext configuration;

    private final ImageTripletRepository repository;

    public NetverifyApiFacade(ConfigurationContext configuration, ImageTripletRepository repository,
            RestTemplate restTemplate) {
        Assert.notNull(configuration, "Configuration context cannot be null!");
        Assert.notNull(repository, "Image repository cannot be null!");
        Assert.notNull(restTemplate, "REST template cannot be null!");
        this.configuration = configuration;
        this.restTemplate = restTemplate;
        httpHeaders = RestTemplateBuilder.buildHttpHeaders();
        this.repository = repository;
    }

    /**
     * Given a configuration context and a collection of image triplets, sends requests to the Netverify API and logs
     * the results.
     *
     * @param imageTriplets A collection of image triplets.
     */
    public void sendApiRequests(List<ImageTriplet> imageTriplets) {
        if (imageTriplets.isEmpty()) {
            logger.warn("No images provided. Exiting.");
            return;
        }

        PerformNetverifyRequest performNetverifyRequest = buildRequest(configuration);
        PresenceStrategy.Validator presenceStrategy = PresenceStrategy.build(configuration.getImagesPresenceStrategy());
        for (ImageTriplet imageTriplet : imageTriplets) {
            if (!imageTriplet.conformsTo(presenceStrategy)) {
                logger.warn("Failed image presence check: {} for {}", presenceStrategy, imageTriplet.getUniqueId());
                continue;
            }
            try {
                setImageData(performNetverifyRequest, imageTriplet);
                makeApiCall(httpHeaders, restTemplate, configuration.getApiServerUrl(), performNetverifyRequest);
                repository.moveAll(imageTriplet, configuration.getImagesSuccessFolder());
            } catch (IOException | RestClientException e) {
                logger.error("Failed sending request: {}", e.getMessage());
                repository.moveAll(imageTriplet, configuration.getImagesFailureFolder());
            } finally {
                clearImageData(performNetverifyRequest);
            }
        }
        logger.info("Finished uploading all images.");
    }

    /**
     * Builds an initial version of the API request object. Any settings that won't change between calls should be set
     * here.
     *
     * @param configuration The configuration context providing the settings.
     * @return An API request object.
     */
    private PerformNetverifyRequest buildRequest(ConfigurationContext configuration) {
        PerformNetverifyRequest performNetverifyRequest = new PerformNetverifyRequest();
        performNetverifyRequest.setEnabledFields(configuration.getApiEnabledFields());
        performNetverifyRequest.setMerchantReportingCriteria(configuration.getApiMerchantReportingCriteria());
        performNetverifyRequest.setCallbackUrl(configuration.getApiCallbackUrl());
        return performNetverifyRequest;
    }

    /**
     * Updates an API request object with variable data (e.g. image data).
     *
     * @param performNetverifyRequest The API request object being updated.
     */
    private void setImageData(PerformNetverifyRequest performNetverifyRequest, ImageTriplet imageTriplet)
            throws IOException {
        performNetverifyRequest.setMerchantIdScanReference(imageTriplet.getUniqueId());
        performNetverifyRequest.setFrontsideImage(asBase64(imageTriplet.getIdFrontImagePath()));
        performNetverifyRequest.setFrontsideImageMimeType(getMimeType(imageTriplet.getIdFrontImagePath()));
        performNetverifyRequest.setFaceImage(asBase64(imageTriplet.getFaceImagePath()));
        performNetverifyRequest.setFaceImageMimeType(getMimeType(imageTriplet.getFaceImagePath()));
        performNetverifyRequest.setBacksideImage(asBase64(imageTriplet.getIdBackImagePath()));
        performNetverifyRequest.setBacksideImageMimeType(getMimeType(imageTriplet.getIdBackImagePath()));
    }

    private void clearImageData(PerformNetverifyRequest performNetverifyRequest) {
        performNetverifyRequest.setMerchantIdScanReference(null);
        performNetverifyRequest.setFaceImage(null);
        performNetverifyRequest.setFaceImageMimeType(null);
        performNetverifyRequest.setBacksideImage(null);
        performNetverifyRequest.setBacksideImageMimeType(null);
        performNetverifyRequest.setFrontsideImage(null);
        performNetverifyRequest.setFrontsideImageMimeType(null);
    }

    /**
     * Sends an HTTP request to the Netverify API.
     *
     * @param httpHeaders The HTTP headers that the HTTP client should use.
     * @param restTemplate The HTTP client.
     * @param serverUrl The URL under which the endpoint can be reached.
     * @param performNetverifyRequest A complete and valid request object.
     */
    private void makeApiCall(HttpHeaders httpHeaders, RestTemplate restTemplate, String serverUrl,
            PerformNetverifyRequest performNetverifyRequest) {
        HttpEntity<PerformNetverifyRequest> httpRequest = new HttpEntity<>(performNetverifyRequest, httpHeaders);
        PerformNetverifyResponse response = restTemplate.postForEntity(serverUrl, httpRequest,
                PerformNetverifyResponse.class).getBody();
        logger.info("Submitted: {}. Jumio scan reference: {}", performNetverifyRequest.getMerchantIdScanReference(),
                response.getJumioIdScanReference());
    }

    /**
     * Encodes the contents of an image file found at the provided path to a Base64 string.
     *
     * @param imagePath The path of an image file.
     * @return A Base64 string.
     */
    private String asBase64(String imagePath) throws IOException {
        if (imagePath == null) {
            return null;
        }
        return Base64.getEncoder().encodeToString(Files.readAllBytes(Paths.get(imagePath)));
    }

    /**
     * Extracts a MIME type from a file name based on the extension. Only supports types accepted by the API.
     *
     * @param imagePath The inspected image path.
     */
    private String getMimeType(String imagePath) {
        if (imagePath == null) {
            return null;
        }
        if (imagePath.endsWith(ConfigurationContext.JPEG_IMAGE)) {
            return MimeTypeUtils.IMAGE_JPEG_VALUE;
        } else if (imagePath.endsWith(ConfigurationContext.PNG_IMAGE)) {
            return MimeTypeUtils.IMAGE_PNG_VALUE;
        } else {
            return "";
        }
    }
}
