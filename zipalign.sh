#!/bin/bash
cd ~/android-sdk-linux/build-tools/$2
./zipalign -v 4 ~/$1/shelfie/shelfie.apk ~/$1/shelfie/shelfie.apk1
cd ~/$1/shelfie/
mv shelfie.apk1 shelfie.apk
