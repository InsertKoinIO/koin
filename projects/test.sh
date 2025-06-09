#!/bin/sh

./gradlew clean test --parallel --no-build-cache --rerun-tasks
