#!/bin/sh

./gradlew clean install
./gradlew dokka assembleRelease publishToMavenLocal

