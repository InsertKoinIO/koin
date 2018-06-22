#!/bin/sh

./gradlew clean test dokka install publishToMavenLocal -c settings-release.gradle

