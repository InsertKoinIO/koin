#!/bin/sh

./gradlew dokka install publishToMavenLocal bintrayUpload --info --no-parallel -c settings-release.gradle