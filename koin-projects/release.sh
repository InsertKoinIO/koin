#!/bin/sh

./test.sh
./install.sh

./gradlew -b bintrayUpload --info -Dorg.gradle.parallel=false