#!/bin/sh

gradle clean test bintrayUpload --info

cd android/
gradle test assembleRelease bintrayUpload --info
