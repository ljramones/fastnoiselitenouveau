#!/bin/bash
# Run the FastNoiseLite Nouveau Preview Tool
# Requires JDK 25

export JAVA_HOME=$(/usr/libexec/java_home -v 25)
mvn -pl preview-tool javafx:run "$@"
