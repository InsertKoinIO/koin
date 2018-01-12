#!/bin/sh

gradle clean install

cd android/ 
gradle assembleRelease publishToMavenLocal

