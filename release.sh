#!/bin/sh

./gradlew clean 
./gradlew install 
./gradlew assembleRelease dokka publishToMavenLocal bintrayUpload --info -Dorg.gradle.parallel=false
