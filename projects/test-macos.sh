#!/bin/sh

./gradlew cleanTest :core:koin-core:macosArm64Test --parallel --no-build-cache
