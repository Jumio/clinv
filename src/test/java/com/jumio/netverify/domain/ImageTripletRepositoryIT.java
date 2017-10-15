/*
 * Jumio Inc.
 *
 * Copyright (C) 2017
 * All rights reserved.
 */
package com.jumio.netverify.domain;

import com.jumio.netverify.ConfigurationContext;
import com.jumio.netverify.Utils;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.TemporaryFolder;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

public class ImageTripletRepositoryIT {

    @Rule
    public final TemporaryFolder tempFolder = new TemporaryFolder();

    @Rule
    public ExpectedException exception = ExpectedException.none();

    private ConfigurationContext configuration;

    private ImageTripletRepository repository;

    @Before
    public void setUp() throws Exception {
        configuration = new ConfigurationContext();
        configuration.load(Collections.emptyList());
        repository = new ImageTripletRepository(configuration);
    }

    @Test
    public void testCtorWithoutConfiguration() {
        exception.expect(IllegalArgumentException.class);
        new ImageTripletRepository(null);
    }

    @Test
    public void testCreateDirectories() throws Exception {
        configuration.load(Utils.propertiesToList(
                Utils.setImageFolderPaths(tempFolder.getRoot().getCanonicalPath(), configuration)));

        repository.createDirectories();

        assertThat(Files.isDirectory(Paths.get(configuration.getImagesSuccessFolder())), is(true));
        assertThat(Files.isDirectory(Paths.get(configuration.getImagesFailureFolder())), is(true));
    }

    @Test
    public void testMoveAll() throws Exception {
        ConfigurationContext configuration = new ConfigurationContext();
        configuration.load(Collections.emptyList());
        configuration.load(Utils.propertiesToList(
                Utils.setImageFolderPaths(tempFolder.getRoot().getCanonicalPath(), configuration)));
        Utils.createAndFillImagesFolder(configuration);

        repository = new ImageTripletRepository(configuration);
        repository.createDirectories();
        List<ImageTriplet> imageTriplets = repository.findAll();
        repository.moveAll(imageTriplets.get(0), configuration.getImagesSuccessFolder());
        assertThat(Files.list(Paths.get(configuration.getImagesSuccessFolder())).count(), is(3L));
    }

    @Test
    public void testMoveAllWithEmptyDestination() throws Exception {
        ConfigurationContext configuration = new ConfigurationContext();
        configuration.load(Collections.emptyList());
        configuration.load(Utils.propertiesToList(
                Utils.setImageFolderPaths(tempFolder.getRoot().getCanonicalPath(), configuration)));
        Utils.createAndFillImagesFolder(configuration);

        repository = new ImageTripletRepository(configuration);
        repository.createDirectories();
        List<ImageTriplet> imageTriplets = repository.findAll();
        repository.moveAll(imageTriplets.get(0), "");
        assertThat(Files.list(Paths.get(configuration.getImagesFolder())).count(), is(3L));
    }


    @Test
    public void testFindAll() throws Exception {
        ConfigurationContext configuration = new ConfigurationContext();
        configuration.load(Collections.emptyList());
        configuration.load(Utils.propertiesToList(
                Utils.setImageFolderPaths(tempFolder.getRoot().getCanonicalPath(), configuration)));
        Utils.createAndFillImagesFolder(configuration);

        repository = new ImageTripletRepository(configuration);
        List<ImageTriplet> imageTriplets = repository.findAll();

        assertThat(imageTriplets, not(empty()));
    }

}