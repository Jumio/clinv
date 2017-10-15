/*
 * Jumio Inc.
 *
 * Copyright (C) 2017
 * All rights reserved.
 */
package com.jumio.netverify.domain;

import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Strategy used when validating the contents of an {@link ImageTriplet}.
 */
public final class PresenceStrategy {

    private static final Logger logger = LoggerFactory.getLogger(PresenceStrategy.class);

    private static final Map<String, Class<? extends Validator>> holder = new HashMap<>(4);

    static {
        holder.put(ID_ONLY.class.getSimpleName(), ID_ONLY.class);
        holder.put(ID_AND_FACE.class.getSimpleName(), ID_AND_FACE.class);
        holder.put(ID_AND_BACK.class.getSimpleName(), ID_AND_BACK.class);
        holder.put(ALL.class.getSimpleName(), ALL.class);
    }

    /**
     * Creates a new image presence strategy instance.
     * <p>
     * This is rather evil, please don't do this at home.
     *
     * @return A pre-configured image presence strategy or a default instance in case of an exception.
     */
    public static Validator build(String strategyClassName) {
        Class<? extends Validator> validatorClass;
        if (holder.containsKey(strategyClassName)) {
            validatorClass = holder.get(strategyClassName);
        } else {
            logger.warn("Cannot find presence strategy: {}.", strategyClassName);
            validatorClass = ID_ONLY.class;
        }
        try {
            Validator validator = validatorClass.newInstance();
            logger.info("Using presence strategy: {}", validator);
            return validator;
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException("Error when instantiating presence strategy!", e.getCause());
        }
    }

    public interface Validator {

        boolean validate(ImageTriplet imageTriplet);

    }

    static final class ID_ONLY implements Validator {

        @Override
        public boolean validate(ImageTriplet imageTriplet) {
            return imageTriplet.hasIdFrontImagePath() && !imageTriplet.hasFaceImagePath() &&
                    !imageTriplet.hasIdBackImagePath();
        }

        @Override
        public String toString() {
            return "Require FRONT only";
        }
    }

    static final class ID_AND_FACE implements Validator {

        @Override
        public boolean validate(ImageTriplet imageTriplet) {
            return imageTriplet.hasIdFrontImagePath() && imageTriplet.hasFaceImagePath() &&
                    !imageTriplet.hasIdBackImagePath();
        }

        @Override
        public String toString() {
            return "Require FRONT and FACE";
        }

    }

    static final class ID_AND_BACK implements Validator {

        @Override
        public boolean validate(ImageTriplet imageTriplet) {
            return imageTriplet.hasIdFrontImagePath() && !imageTriplet.hasFaceImagePath() &&
                    imageTriplet.hasIdBackImagePath();
        }

        @Override
        public String toString() {
            return "Require FRONT and BACK";
        }
    }

    static final class ALL implements Validator {

        @Override
        public boolean validate(ImageTriplet imageTriplet) {
            return imageTriplet.hasIdFrontImagePath() && imageTriplet.hasFaceImagePath() &&
                    imageTriplet.hasIdBackImagePath();
        }

        @Override
        public String toString() {
            return "Require FRONT, FACE and BACK";
        }
    }
}
