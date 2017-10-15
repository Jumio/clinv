/*
 * Jumio Inc.
 *
 * Copyright (C) 2017
 * All rights reserved.
 */
package com.jumio.netverify.cli;

import com.jumio.netverify.ConfigurationContext;
import com.jumio.netverify.Utils;
import java.util.Collections;
import java.util.Properties;
import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.SystemOutRule;
import org.junit.rules.TemporaryFolder;

public class PerformNetverifyIT {

    @Rule
    public final SystemOutRule systemOut = new SystemOutRule().enableLog();

    @Rule
    public final TemporaryFolder tempFolder = new TemporaryFolder();

    @Test
    public void testMainWithoutImages() throws Exception {
        ConfigurationContext configuration = new ConfigurationContext();
        configuration.load(Collections.emptyList());
        String imagesFolderPath = tempFolder.getRoot().getCanonicalPath();
        tempFolder.newFolder(configuration.getImagesFolder());

        Properties cliProperties = Utils.setImageFolderPaths(imagesFolderPath, configuration);
        cliProperties.put("api.token", "foo");
        cliProperties.put("api.secret", "bar");

        PerformNetverify.main(Utils.propertiesToArray(cliProperties));
    }

}
