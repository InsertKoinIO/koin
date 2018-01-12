#!/bin/sh

gradle clean test

cd android/
gradle clean testRelease
