#!/bin/bash
# Run the FastNoiseLite Nouveau Samples Launcher
# Requires JDK 25

export JAVA_HOME=$(/usr/libexec/java_home -v 25)

# Ensure noisegen-lib is installed (required dependency)
mvn -pl noisegen-lib install -DskipTests -q

mvn -pl samples javafx:run "$@"
