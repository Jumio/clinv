/*
 * Jumio Inc.
 *
 * Copyright (C) 2017
 * All rights reserved.
 */
package com.jumio.netverify.rest;

/**
 * DTO used when receiving a response from the Netverify v2 "perform" API endpoint.
 */
final class PerformNetverifyResponse {

    private String jumioIdScanReference;

    private String timestamp;

    public PerformNetverifyResponse() {
    }

    public String getJumioIdScanReference() {
        return jumioIdScanReference;
    }

    public void setJumioIdScanReference(String jumioIdScanReference) {
        this.jumioIdScanReference = jumioIdScanReference;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
