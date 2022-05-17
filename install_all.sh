#!/bin/sh

cd ./core
./install.sh
cd ..

cd ./android
./install.sh
cd ..

cd ./plugins
./install.sh
cd ..
