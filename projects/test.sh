#!/bin/sh

# test: JVM + Android modules / jvmTest: KMP modules' JVM tests (koin-core, koin-test, koin-ktor...)
# KMP modules have no plain `test` task, so without jvmTest their suites are silently skipped.
./gradlew clean test jvmTest --parallel --no-build-cache --rerun-tasks
