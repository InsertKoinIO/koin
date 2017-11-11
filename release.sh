#!/bin/sh

gradle clean test install bintrayUpload --info
cd android/
gradle clean test assembleRelease install bintrayUpload --info
