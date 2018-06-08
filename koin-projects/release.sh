#!/bin/sh

./gradlew clean build dokka install publishToMavenLocal bintrayUpload --info