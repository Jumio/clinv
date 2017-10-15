/*
 * Jumio Inc.
 *
 * Copyright (C) 2017
 * All rights reserved.
 */
package com.jumio.netverify.domain;

import com.jumio.netverify.ConfigurationContext;
import java.io.IOException;
import java.nio.file.DirectoryIteratorException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/**
 * Repository of {@link ImageTriplet}s.
 * <p>
 * Provides basic CRUD-like operations. Backed by a file system.
 */
public final class ImageTripletRepository {

    private static final Logger logger = LoggerFactory.getLogger(ImageTripletRepository.class);

    private final ConfigurationContext configuration;

    private final Map<String, ImageTriplet> imageTriplets;

    private final Pattern imageNamePattern;

    private final List<String> invalidUniqueIds;

    public ImageTripletRepository(ConfigurationContext configuration) {
        Assert.notNull(configuration, "Configuration context cannot be null!");
        this.configuration = configuration;
        imageTriplets = new HashMap<>();
        invalidUniqueIds = new ArrayList<>();
        imageNamePattern = Pattern.compile(configuration.getImagesNamePattern());
    }

    public List<ImageTriplet> findAll() throws IOException {
        logger.info("Parsing image names in {}", configuration.getImagesFolder());
        try (DirectoryStream<Path> imagePaths = Files.newDirectoryStream(Paths.get(configuration.getImagesFolder()),
                "*.{jpg,png}")) {
            for (Path imagePath : imagePaths) {
                appendToImageTriplets(imagePath);
            }
        } catch (DirectoryIteratorException ex) {
            throw ex.getCause();
        }
        return new ArrayList<>(imageTriplets.values());
    }

    public void createDirectories() throws IOException {
        Path successFolder = Paths.get(configuration.getImagesSuccessFolder());
        Path failureFolder = Paths.get(configuration.getImagesFailureFolder());
        createDirectory(successFolder);
        createDirectory(failureFolder);
    }

    public void moveAll(ImageTriplet imageTriplet, String destination) {
        if (!StringUtils.hasText(destination)) {
            logger.warn("Skipping moving images for ID {}. Empty path provided!", imageTriplet.getUniqueId());
            return;
        }
        Path destinationPath = Paths.get(destination);
        try {
            move(imageTriplet.getIdFrontImagePath(), destinationPath);
            move(imageTriplet.getFaceImagePath(), destinationPath);
            move(imageTriplet.getIdBackImagePath(), destinationPath);
        } catch (IOException ioe) {
            logger.error("Error when moving images to {} folder: {}", destinationPath, ioe.getMessage());
        }
    }

    private void appendToImageTriplets(Path imagePath) {
        String fileName = imagePath.getFileName() == null ? "" : imagePath.getFileName().toString();
        Matcher matcher = imageNamePattern.matcher(fileName);
        if (matcher.matches()) {
            String uniqueId = matcher.group(1);
            String classifier = matcher.group(2);
            if (invalidUniqueIds.contains(uniqueId)) {
                return;
            }
            ImageTriplet imageTriplet = imageTriplets.computeIfAbsent(uniqueId, ImageTriplet::new);
            try {
                imageTriplet.update(imagePath.toString(), classifier, configuration);
            } catch (DuplicateImageException | UnknownClassifierException e) {
                logger.warn("Skipping all images with ID: {}. Reason: {}", uniqueId, e.getMessage());
                imageTriplets.remove(uniqueId);
                invalidUniqueIds.add(uniqueId);
            }
        } else {
            logger.warn("Could not parse image name: {}", imagePath.getFileName());
        }
    }

    private void createDirectory(Path path) throws IOException {
        if (!Files.isDirectory(path)) {
            Files.createDirectory(path);
            logger.info("Creating new image directory: {}", path);
        } else {
            logger.info("Reusing existing image directory: {}", path);
        }
    }

    private void move(String source, Path destinationPath) throws IOException {
        if (StringUtils.hasText(source)) {
            Path sourcePath = Paths.get(source);
            Files.move(sourcePath, destinationPath.resolve(sourcePath.getFileName()));
            logger.info("Moved {} to {}", sourcePath, destinationPath);
        }
    }
}
