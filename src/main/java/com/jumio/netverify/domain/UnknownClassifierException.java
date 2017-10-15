/*
 * Jumio Inc.
 *
 * Copyright (C) 2017
 * All rights reserved.
 */
package com.jumio.netverify.domain;

/**
 * Thrown when an unknown classifier is found in an image name.
 */
class UnknownClassifierException extends Exception {

    UnknownClassifierException(String message) {
        super(message);
    }
}
