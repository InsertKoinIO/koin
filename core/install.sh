#!/bin/sh

../gradlew publishToMavenLocal;
../gradlew :koin-core-ext:install :koin-test-junit4:install :koin-test-junit5:install

