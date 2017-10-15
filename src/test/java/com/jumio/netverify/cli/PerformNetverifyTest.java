/*
 * Jumio Inc.
 *
 * Copyright (C) 2017
 * All rights reserved.
 */

package com.jumio.netverify.cli;

import com.jumio.netverify.ConfigurationContext;
import com.jumio.netverify.domain.ImageTripletRepository;
import java.io.IOException;
import java.util.Collections;
import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.ExpectedSystemExit;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class PerformNetverifyTest {

    private static final int EXPECTED_FAILURE_STATUS = 255;

    @Rule
    public final ExpectedSystemExit exit = ExpectedSystemExit.none();

    @Test
    public void testAppExitsOnMissingApiCredentials() {
        exit.expectSystemExitWithStatus(EXPECTED_FAILURE_STATUS);
        PerformNetverify.main(new String[0]);
    }

    @Test
    public void testAppExitsWhenConfigurationLoadingFails() throws Exception {
        exit.expectSystemExitWithStatus(EXPECTED_FAILURE_STATUS);
        ConfigurationContext configurationContext = mock(ConfigurationContext.class);
        when(configurationContext.load(anyList())).thenThrow(new IOException("unreadable test properties"));

        PerformNetverify.loadConfiguration(Collections.emptyList(), configurationContext);
    }

    @Test
    public void testAppExitsWhenDirectoriesCannotBeCreated() throws Exception {
        exit.expectSystemExitWithStatus(EXPECTED_FAILURE_STATUS);
        ImageTripletRepository repository = mock(ImageTripletRepository.class);
        doThrow(new IOException("test image destination directory")).when(repository).createDirectories();

        PerformNetverify.createRequiredDirectories(repository);
    }

    @Test
    public void testAppExitsWhenImagesCannotBeLoaded() throws Exception {
        exit.expectSystemExitWithStatus(EXPECTED_FAILURE_STATUS);
        ImageTripletRepository repository = mock(ImageTripletRepository.class);
        doThrow(new IOException("test image source directory")).when(repository).findAll();

        PerformNetverify.loadImages(repository);
    }
}