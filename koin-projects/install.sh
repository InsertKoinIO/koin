#!/bin/sh

./gradlew clean test build dokka install publishToMavenLocal

