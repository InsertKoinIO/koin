#!/bin/sh

gradle clean test dokka install bintrayUpload --info
cd android/
gradle clean test dokka assembleRelease install bintrayUpload --info
