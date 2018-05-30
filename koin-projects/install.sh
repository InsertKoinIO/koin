#!/bin/sh

./gradlew clean dokka install
./gradlew assembleRelease publishToMavenLocal

