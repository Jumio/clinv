/*
 * Jumio Inc.
 *
 * Copyright (C) 2017
 * All rights reserved.
 */
package com.jumio.netverify.domain;

import com.jumio.netverify.ConfigurationContext;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/**
 * Container for image paths. Used during an API call as a data source. Currently overwriting already set image paths is
 * not supported.
 */
public final class ImageTriplet {

    private final String uniqueId;

    private String idFrontImagePath;

    private String idBackImagePath;

    private String faceImagePath;

    ImageTriplet(String uniqueId) {
        Assert.notNull(uniqueId, "Unique image triplet ID cannot be null!");
        this.uniqueId = uniqueId;
    }

    void update(String imagePath, String classifier, ConfigurationContext configuration)
            throws DuplicateImageException, UnknownClassifierException {
        if (classifier.contains(configuration.getImagesFrontSuffix())) {
            setIdFrontImagePath(imagePath);
        } else if (classifier.contains(configuration.getImagesBackSuffix())) {
            setIdBackImagePath(imagePath);
        } else if (classifier.contains(configuration.getImagesFaceSuffix())) {
            setFaceImagePath(imagePath);
        } else {
            throw new UnknownClassifierException("Found image with unknown classifier: " + imagePath);
        }
    }

    public boolean conformsTo(PresenceStrategy.Validator presenceStrategy) {
        return presenceStrategy.validate(this);
    }

    public String getUniqueId() {
        return uniqueId;
    }

    public String getIdFrontImagePath() {
        return idFrontImagePath;
    }

    private void setIdFrontImagePath(String idFrontImagePath) throws DuplicateImageException {
        if (hasIdFrontImagePath()) {
            throw new DuplicateImageException("Found duplicate ID front image: " + getIdFrontImagePath());
        }
        this.idFrontImagePath = idFrontImagePath;
    }

    public String getIdBackImagePath() {
        return idBackImagePath;
    }

    private void setIdBackImagePath(String idBackImagePath) throws DuplicateImageException {
        if (hasIdBackImagePath()) {
            throw new DuplicateImageException("Found duplicate ID back image: " + getIdBackImagePath());
        }
        this.idBackImagePath = idBackImagePath;
    }

    public String getFaceImagePath() {
        return faceImagePath;
    }

    private void setFaceImagePath(String faceImagePath) throws DuplicateImageException {
        if (hasFaceImagePath()) {
            throw new DuplicateImageException("Found duplicate face image: " + getFaceImagePath());
        }
        this.faceImagePath = faceImagePath;
    }

    boolean hasFaceImagePath() {
        return !StringUtils.isEmpty(faceImagePath);
    }

    boolean hasIdBackImagePath() {
        return !StringUtils.isEmpty(idBackImagePath);
    }

    boolean hasIdFrontImagePath() {
        return !StringUtils.isEmpty(idFrontImagePath);
    }

}
