#!/bin/bash

cd "/opt/owlet" || { echo "Do not access work dir.";  exit 1; }

JAVA_HOME="/opt/owlet/runtime/"

export PATH="$JAVA_HOME/bin:$PATH"

JAR_FILE="/opt/owlet/owlet.jar"

$JAVA_HOME/bin/java -jar "$JAR_FILE"