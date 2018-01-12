#!/bin/sh

gradle clean test bintrayUpload --info

cd android/
gradle clean test assembleRelease bintrayUpload --info
