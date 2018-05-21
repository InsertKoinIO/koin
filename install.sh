#!/bin/sh

./gradlew -b ./core/build.gradle clean install

./gradlew -b ./android/build.gradle clean assembleRelease publishToMavenLocal

./gradlew -b ./web/build.gradle clean install

