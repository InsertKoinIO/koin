#!/bin/sh

./test.sh
./install.sh

./gradlew bintrayUpload --info -Dorg.gradle.parallel=false