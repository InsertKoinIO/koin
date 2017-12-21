#!/bin/sh

gradle clean test --info

cd android/
gradle clean testRelease --info
cd ..

cd samples

cd android-weatherapp
cd app
gradle clean testRelease --info
cd ..

cd kotlin-sampleapp
gradle clean test --info
cd ..

cd sparkjava-sampleapp
gradle clean test --info
cd ..

cd ktor-starter
gradle clean test --info
cd ..