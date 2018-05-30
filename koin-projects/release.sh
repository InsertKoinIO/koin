#!/bin/sh

./test.sh
./install.sh

./gradlew -b ./core/build.gradle dokka bintrayUpload --info -Dorg.gradle.parallel=false
./gradlew -b ./android/build.gradle bintrayUpload --info -Dorg.gradle.parallel=false
./gradlew -b ./web/build.gradle bintrayUpload --info -Dorg.gradle.parallel=false