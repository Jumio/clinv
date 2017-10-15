/*
 * Jumio Inc.
 *
 * Copyright (C) 2017
 * All rights reserved.
 */
package com.jumio.netverify.domain;

/**
 * Thrown when a duplicate image with the same classifier is found.
 */
class DuplicateImageException extends Exception {

    DuplicateImageException(String message) {
        super(message);
    }
}
