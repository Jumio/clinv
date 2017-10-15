/*
 * Jumio Inc.
 *
 * Copyright (C) 2017
 * All rights reserved.
 */
package com.jumio.netverify.rest;

import java.time.LocalDateTime;
import org.springframework.util.MimeTypeUtils;

/**
 * DTO used when calling the Netverify v2 "perform" API endpoint.
 */
public final class PerformNetverifyRequest {

    private String enabledFields;

    private String merchantIdScanReference;

    private String merchantReportingCriteria;

    private String customerId;

    private String additionalInformation;

    private String callbackUrl;

    private String firstName;

    private String lastName;

    private String country;

    private String usState;

    private LocalDateTime expiry;

    private String number;

    private IdType idType;

    private LocalDateTime dob;

    private String frontsideImage;

    private String frontsideImageMimeType = MimeTypeUtils.IMAGE_JPEG_VALUE;

    private String backsideImage;

    private String backsideImageMimeType = MimeTypeUtils.IMAGE_JPEG_VALUE;

    private CallbackGranularity callbackGranularity;

    private String personalNumber;

    private Boolean mrzCheck;

    private String faceImage;

    private String faceImageMimeType = MimeTypeUtils.IMAGE_JPEG_VALUE;

    public PerformNetverifyRequest() {
    }

    public String getEnabledFields() {
        return enabledFields;
    }

    public void setEnabledFields(String enabledFields) {
        this.enabledFields = enabledFields;
    }

    public String getMerchantIdScanReference() {
        return merchantIdScanReference;
    }

    public void setMerchantIdScanReference(String merchantIdScanReference) {
        this.merchantIdScanReference = merchantIdScanReference;
    }

    public String getMerchantReportingCriteria() {
        return merchantReportingCriteria;
    }

    public void setMerchantReportingCriteria(String merchantReportingCriteria) {
        this.merchantReportingCriteria = merchantReportingCriteria;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getAdditionalInformation() {
        return additionalInformation;
    }

    public void setAdditionalInformation(String additionalInformation) {
        this.additionalInformation = additionalInformation;
    }

    public String getCallbackUrl() {
        return callbackUrl;
    }

    public void setCallbackUrl(String callbackUrl) {
        this.callbackUrl = callbackUrl;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getUsState() {
        return usState;
    }

    public void setUsState(String usState) {
        this.usState = usState;
    }

    public LocalDateTime getExpiry() {
        return expiry;
    }

    public void setExpiry(LocalDateTime expiry) {
        this.expiry = expiry;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public IdType getIdType() {
        return idType;
    }

    public void setIdType(IdType idType) {
        this.idType = idType;
    }

    public LocalDateTime getDob() {
        return dob;
    }

    public void setDob(LocalDateTime dob) {
        this.dob = dob;
    }

    public String getFrontsideImage() {
        return frontsideImage;
    }

    public void setFrontsideImage(String frontsideImage) {
        this.frontsideImage = frontsideImage;
    }

    public String getFrontsideImageMimeType() {
        return frontsideImageMimeType;
    }

    public void setFrontsideImageMimeType(String frontsideImageMimeType) {
        this.frontsideImageMimeType = frontsideImageMimeType;
    }

    public String getBacksideImage() {
        return backsideImage;
    }

    public void setBacksideImage(String backsideImage) {
        this.backsideImage = backsideImage;
    }

    public String getBacksideImageMimeType() {
        return backsideImageMimeType;
    }

    public void setBacksideImageMimeType(String backsideImageMimeType) {
        this.backsideImageMimeType = backsideImageMimeType;
    }

    public CallbackGranularity getCallbackGranularity() {
        return callbackGranularity;
    }

    public void setCallbackGranularity(CallbackGranularity callbackGranularity) {
        this.callbackGranularity = callbackGranularity;
    }

    public String getPersonalNumber() {
        return personalNumber;
    }

    public void setPersonalNumber(String personalNumber) {
        this.personalNumber = personalNumber;
    }

    public Boolean getMrzCheck() {
        return mrzCheck;
    }

    public void setMrzCheck(Boolean mrzCheck) {
        this.mrzCheck = mrzCheck;
    }

    public String getFaceImage() {
        return faceImage;
    }

    public void setFaceImage(String faceImage) {
        this.faceImage = faceImage;
    }

    public String getFaceImageMimeType() {
        return faceImageMimeType;
    }

    public void setFaceImageMimeType(String faceImageMimeType) {
        this.faceImageMimeType = faceImageMimeType;
    }

    public enum IdType {PASSPORT, ID_CARD, DRIVING_LICENSE, VISA}

    public enum CallbackGranularity {onAllSteps, onFinish}
}
