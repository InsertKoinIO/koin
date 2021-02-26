#!/bin/sh

../gradlew publishToMavenLocal;
../gradlew :koin-core-ext:publishToMavenLocal :koin-test-junit4:publishToMavenLocal :koin-test-junit5:publishToMavenLocal

