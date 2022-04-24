#!/bin/sh

cd ./core
./test.sh
cd ..

cd ./android
./test.sh
cd ..

cd ./plugins
./test.sh
cd ..

cd ./examples
./test.sh
cd ..
