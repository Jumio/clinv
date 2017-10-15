/*
 * Jumio Inc.
 *
 * Copyright (C) 2017
 * All rights reserved.
 */
package com.jumio.netverify;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Properties;
import java.util.UUID;
import java.util.stream.Collectors;

public class Utils {

    public static List<String> propertiesToList(Properties properties) {
        return properties.entrySet().stream().map(e -> e.getKey() + "=" + e.getValue()).collect(Collectors.toList());
    }

    public static String[] propertiesToArray(Properties properties) {
        return propertiesToList(properties).toArray(new String[properties.size()]);
    }

    public static Properties setImageFolderPaths(String tempFolderPath, ConfigurationContext configuration)
            throws Exception {
        Properties properties = new Properties();
        properties.put("images.successFolder",
                tempFolderPath + File.separator + configuration.getImagesSuccessFolder());
        properties.put("images.failureFolder",
                tempFolderPath + File.separator + configuration.getImagesFailureFolder());
        properties.put("images.folder", tempFolderPath + File.separator + configuration.getImagesFolder());
        return properties;
    }

    public static void createAndFillImagesFolder(ConfigurationContext configuration) throws Exception {
        Path imagesDirectory = Files.createDirectory(Paths.get(configuration.getImagesFolder()));
        String uniqueId = UUID.randomUUID().toString();
        Files.createFile(imagesDirectory
                .resolve(uniqueId + "_" + configuration.getImagesFrontSuffix() + ConfigurationContext.JPEG_IMAGE));
        Files.createFile(imagesDirectory
                .resolve(uniqueId + "_" + configuration.getImagesBackSuffix() + ConfigurationContext.JPEG_IMAGE));
        Files.createFile(imagesDirectory
                .resolve(uniqueId + "_" + configuration.getImagesFaceSuffix() + ConfigurationContext.JPEG_IMAGE));
    }

}
