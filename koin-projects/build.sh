#!/bin/sh

./gradlew :koin-core:build
#./gradlew :koin-test:build -Dorg.gradle.parallel=false 
./gradlew :koin-core-ext:build
./gradlew :koin-android:build
./gradlew :koin-ktor:build
