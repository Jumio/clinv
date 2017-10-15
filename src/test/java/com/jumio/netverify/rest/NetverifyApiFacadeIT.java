/*
 * Jumio Inc.
 *
 * Copyright (C) 2017
 * All rights reserved.
 */
package com.jumio.netverify.rest;

import com.jumio.netverify.ConfigurationContext;
import com.jumio.netverify.Utils;
import com.jumio.netverify.domain.ImageTriplet;
import com.jumio.netverify.domain.ImageTripletRepository;
import com.jumio.netverify.domain.PresenceStrategy;
import java.util.Collections;
import java.util.Properties;
import java.util.UUID;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

public class NetverifyApiFacadeIT {

    @Rule
    public final TemporaryFolder tempFolder = new TemporaryFolder();

    private RestTemplate restTemplate;

    private ImageTripletRepository repository;

    private ConfigurationContext configuration;

    @Before
    public void setUp() throws Exception {
        restTemplate = mock(RestTemplate.class);
        repository = mock(ImageTripletRepository.class);
        configuration = buildConfiguration();
    }

    @Test
    public void testSendApiRequestsWithoutImages() {
        NetverifyApiFacade facade = new NetverifyApiFacade(configuration, repository, restTemplate);
        facade.sendApiRequests(Collections.emptyList());

        verifyZeroInteractions(restTemplate);
        verifyZeroInteractions(repository);
    }

    @Test
    public void testSendApiRequestsWithImage() throws Exception {
        String path = tempFolder.newFile().getCanonicalPath();
        ImageTriplet triplet = mock(ImageTriplet.class);
        ResponseEntity responseEntity = mock(ResponseEntity.class);

        when(triplet.getUniqueId()).thenReturn(UUID.randomUUID().toString());
        when(triplet.getFaceImagePath()).thenReturn(path);
        when(triplet.getIdBackImagePath()).thenReturn(path);
        when(triplet.getIdFrontImagePath()).thenReturn(path);
        when(triplet.conformsTo(any(PresenceStrategy.Validator.class))).thenReturn(true);
        when(restTemplate.postForEntity(anyString(), any(HttpEntity.class), any(Class.class))).thenReturn(
                responseEntity);
        when(responseEntity.getBody()).thenReturn(new PerformNetverifyResponse());

        NetverifyApiFacade facade = new NetverifyApiFacade(configuration, repository, restTemplate);
        facade.sendApiRequests(Collections.singletonList(triplet));

        verify(restTemplate, times(1)).postForEntity(anyString(), any(HttpEntity.class), any(Class.class));
        verify(repository, times(1)).moveAll(eq(triplet), eq(configuration.getImagesSuccessFolder()));
    }

    @Test
    public void testSendApiRequestsWithValidationError() throws Exception {
        ImageTriplet triplet = mock(ImageTriplet.class);
        when(triplet.conformsTo(any(PresenceStrategy.Validator.class))).thenReturn(false);

        NetverifyApiFacade facade = new NetverifyApiFacade(configuration, repository, restTemplate);
        facade.sendApiRequests(Collections.singletonList(triplet));

        verifyZeroInteractions(restTemplate);
        verifyZeroInteractions(repository);
    }

    @Test
    public void testSendApiRequestsWithRestException() throws Exception {
        String path = tempFolder.newFile().getCanonicalPath();
        ImageTriplet triplet = mock(ImageTriplet.class);
        RestClientException exception = new RestClientException("test caused exception");

        when(triplet.getUniqueId()).thenReturn(UUID.randomUUID().toString());
        when(triplet.getFaceImagePath()).thenReturn(path);
        when(triplet.getIdBackImagePath()).thenReturn(path);
        when(triplet.getIdFrontImagePath()).thenReturn(path);
        when(triplet.conformsTo(any(PresenceStrategy.Validator.class))).thenReturn(true);
        when(restTemplate.postForEntity(anyString(), any(HttpEntity.class), any(Class.class))).thenThrow(exception);

        NetverifyApiFacade facade = new NetverifyApiFacade(configuration, repository, restTemplate);
        facade.sendApiRequests(Collections.singletonList(triplet));

        verify(restTemplate, times(1)).postForEntity(anyString(), any(HttpEntity.class), any(Class.class));
        verify(repository, times(1)).moveAll(eq(triplet), eq(configuration.getImagesFailureFolder()));
    }

    private ConfigurationContext buildConfiguration() throws Exception {
        Properties cliProperties = new Properties();
        cliProperties.setProperty("api.serverUrl", "http://localhost");
        cliProperties.setProperty("api.token", "foo");
        cliProperties.setProperty("api.secret", "bar");
        ConfigurationContext configuration = new ConfigurationContext();
        configuration.load(Utils.propertiesToList(cliProperties));
        return configuration;
    }
}