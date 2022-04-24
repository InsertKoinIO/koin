#!/bin/sh

cd ./core
./release.sh
cd ..

cd ./android
./release.sh
cd ..

cd ./plugins
./release.sh
cd ..
