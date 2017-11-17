#!/bin/sh

gradle clean test --info

cd android/
gradle clean testRelease --info
cd ..

cd samples

cd android-weatherapp
cd app
gradle clean testRelease --info
