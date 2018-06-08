#!/bin/sh

./gradlew clean dokka install --info
./gradlew assembleRelease publishToMavenLocal --info

