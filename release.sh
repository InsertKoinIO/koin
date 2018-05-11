#!/bin/sh

./gradlew clean 
./gradlew test install 
./gradlew assembleRelease dokka publishToMavenLocal bintrayUpload --info -Dorg.gradle.parallel=false