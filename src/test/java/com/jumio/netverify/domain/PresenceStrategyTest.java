/*
 * Jumio Inc.
 *
 * Copyright (C) 2017
 * All rights reserved.
 */
package com.jumio.netverify.domain;

import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class PresenceStrategyTest {

    private ImageTriplet imageTriplet;

    @Before
    public void setUp() {
        imageTriplet = mock(ImageTriplet.class);
    }

    @Test
    public void testUnknownPresenceStrategy() {
        PresenceStrategy.Validator validator = PresenceStrategy.build("foo");
        assertThat(validator, is(notNullValue()));
        assertThat(validator.getClass(), is(PresenceStrategy.ID_ONLY.class));
    }

    @Test
    public void testIdOnlyValidator() {
        when(imageTriplet.hasIdFrontImagePath()).thenReturn(true);
        when(imageTriplet.hasFaceImagePath()).thenReturn(false);
        when(imageTriplet.hasIdBackImagePath()).thenReturn(false);

        PresenceStrategy.Validator validator = PresenceStrategy.build("ID_ONLY");

        assertThat(validator.validate(imageTriplet), is(true));
    }

    @Test
    public void testIdAndFaceValidator() {
        when(imageTriplet.hasIdFrontImagePath()).thenReturn(true);
        when(imageTriplet.hasFaceImagePath()).thenReturn(true);
        when(imageTriplet.hasIdBackImagePath()).thenReturn(false);

        PresenceStrategy.Validator validator = PresenceStrategy.build("ID_AND_FACE");

        assertThat(validator.validate(imageTriplet), is(true));
    }

    @Test
    public void testIdAndBackValidator() {
        when(imageTriplet.hasIdFrontImagePath()).thenReturn(true);
        when(imageTriplet.hasFaceImagePath()).thenReturn(false);
        when(imageTriplet.hasIdBackImagePath()).thenReturn(true);

        PresenceStrategy.Validator validator = PresenceStrategy.build("ID_AND_BACK");

        assertThat(validator.validate(imageTriplet), is(true));
    }

    @Test
    public void testAllValidator() {
        when(imageTriplet.hasIdFrontImagePath()).thenReturn(true);
        when(imageTriplet.hasFaceImagePath()).thenReturn(true);
        when(imageTriplet.hasIdBackImagePath()).thenReturn(true);

        PresenceStrategy.Validator validator = PresenceStrategy.build("ALL");

        assertThat(validator.validate(imageTriplet), is(true));
    }

    @Test
    public void testFailedValidation() {
        when(imageTriplet.hasIdFrontImagePath()).thenReturn(true);
        when(imageTriplet.hasFaceImagePath()).thenReturn(false);
        when(imageTriplet.hasIdBackImagePath()).thenReturn(false);

        PresenceStrategy.Validator validator = PresenceStrategy.build("ALL");

        assertThat(validator.validate(imageTriplet), is(false));
    }

}