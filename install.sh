#!/bin/sh

gradle clean test install

cd android/
gradle clean test assembleRelease install
