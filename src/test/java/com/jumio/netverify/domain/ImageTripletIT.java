/*
 * Jumio Inc.
 *
 * Copyright (C) 2017
 * All rights reserved.
 */
package com.jumio.netverify.domain;

import com.jumio.netverify.ConfigurationContext;
import java.util.Collections;
import java.util.UUID;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.TemporaryFolder;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.emptyString;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class ImageTripletIT {

    @Rule
    public final TemporaryFolder tempFolder = new TemporaryFolder();

    @Rule
    public ExpectedException exception = ExpectedException.none();

    private ImageTriplet imageTriplet;

    private ConfigurationContext configuration;

    @Before
    public void setUp() throws Exception {
        imageTriplet = new ImageTriplet(UUID.randomUUID().toString());
        configuration = new ConfigurationContext();
        configuration.load(Collections.emptyList());
    }

    @Test
    public void testCtorThrowsExceptionOnNullUniqueId() {
        exception.expect(IllegalArgumentException.class);
        new ImageTriplet(null);
    }

    @Test
    public void testCtor() {
        assertThat(imageTriplet, notNullValue());
        assertThat(imageTriplet.getUniqueId(), is(not(emptyString())));
    }

    @Test
    public void testUpdateThrowsUnknownClassifierException() throws Exception {
        exception.expect(UnknownClassifierException.class);
        imageTriplet.update(tempFolder.newFile().getCanonicalPath(), "foobar", configuration);
    }

    @Test
    public void testUpdateFrontWithDuplicateThrows() throws Exception {
        exception.expect(DuplicateImageException.class);
        String frontClassifier = configuration.getImagesFrontSuffix();
        String imagePath = tempFolder.newFile().getCanonicalPath();
        imageTriplet.update(imagePath, imagePath + "_" + frontClassifier, configuration);
        imageTriplet.update(imagePath, imagePath + "_" + frontClassifier, configuration);
    }

    @Test
    public void testUpdateBackWithDuplicateThrows() throws Exception {
        exception.expect(DuplicateImageException.class);
        String backClassifier = configuration.getImagesBackSuffix();
        String imagePath = tempFolder.newFile().getCanonicalPath();
        imageTriplet.update(imagePath, imagePath + "_" + backClassifier, configuration);
        imageTriplet.update(imagePath, imagePath + "_" + backClassifier, configuration);
    }

    @Test
    public void testUpdateFaceWithDuplicateThrows() throws Exception {
        exception.expect(DuplicateImageException.class);
        String faceClassifier = configuration.getImagesFaceSuffix();
        String imagePath = tempFolder.newFile().getCanonicalPath();
        imageTriplet.update(imagePath, imagePath + "_" + faceClassifier, configuration);
        imageTriplet.update(imagePath, imagePath + "_" + faceClassifier, configuration);
    }

    @Test
    public void testUpdateFaceImage() throws Exception {
        String faceClassifier = configuration.getImagesFaceSuffix();
        String imagePath = tempFolder.newFile().getCanonicalPath();
        imageTriplet.update(imagePath, imagePath + "_" + faceClassifier, configuration);
        assertThat(imageTriplet.getFaceImagePath(), is(imagePath));
    }

    @Test
    public void testUpdateFrontImage() throws Exception {
        String frontClassifier = configuration.getImagesFrontSuffix();
        String imagePath = tempFolder.newFile().getCanonicalPath();
        imageTriplet.update(imagePath, imagePath + "_" + frontClassifier, configuration);
        assertThat(imageTriplet.getIdFrontImagePath(), is(imagePath));
    }

    @Test
    public void testUpdateBackImage() throws Exception {
        String backClassifier = configuration.getImagesBackSuffix();
        String imagePath = tempFolder.newFile().getCanonicalPath();
        imageTriplet.update(imagePath, imagePath + "_" + backClassifier, configuration);
        assertThat(imageTriplet.getIdBackImagePath(), is(imagePath));
    }

    @Test
    public void testConformsTo() {
        PresenceStrategy.Validator validator = mock(PresenceStrategy.Validator.class);
        imageTriplet.conformsTo(validator);
        verify(validator, times(1)).validate(eq(imageTriplet));
    }

}