#!/bin/sh

./gradlew clean install
./gradlew assembleRelease publishToMavenLocal

