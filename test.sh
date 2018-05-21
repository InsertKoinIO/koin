#!/bin/sh

./gradlew -b ./core/build.gradle clean test
./gradlew -b ./android/build.gradle clean testRelease
./gradlew -b ./web/build.gradle clean test
