# Summary

CLI tool that can be used by merchants to test the Netverify "perform" service. No Spring Boot dependencies are used.
The tool tries to be flexible with regard to the image naming patterns used by the merchant.

Nevertheless, the images have to be properly classified before the tool can be used:
- no duplicates with the same classifier are allowed (such a case will be logged and the images with a corresponding
unique ID skipped),
- the number of images inside a unique ID group has to correspond to the chosen image presence strategy (e.g. one 
image if just the ID front is sent etc.).

# Build

The POM is kept explicitly as small as possible. The final JAR is built with dependencies by the Maven assembly plugin.
Running

`mvn clean package`

will produce the JAR itself and additionally a ZIP file inside which the JAR and a configuration file are located.

# Configure

The tool supports three types of configurations:
- an internal `config.properties` file, filled with default values,
- an external `config.properties` file, overriding the internal one completely,
- CLI parameters that override the `config.properties` values.

The properties are documented inside the configuration file. All values can be overridden by a CLI parameter. 

# Run

The tool can be started using

`java -jar perform-nv-cli.jar api.token=******** api.secret=********`

The `api.token` and `api.secret` parameters are the only two that have to be specified on the command line. They can be
persisted in the `config.properties`, but such a use case is discouraged due to security reasons.