#!/bin/sh

./gradlew :koin-core-ext:install :koin-gradle-plugin:install :koin-ktor:install :koin-logger-slf4j:install

./gradlew publishToMavenLocal -Dorg.gradle.parallel=false

