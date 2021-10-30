#!/bin/sh

../gradlew publishToMavenLocal;
../gradlew :koin-test-junit4:publishToMavenLocal :koin-test-junit5:publishToMavenLocal

