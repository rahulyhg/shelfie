#!/bin/bash
cd ~/android-sdk-linux/build-tools/20.0.0/
./zipalign -v 4 ~/play/shelfie/shelfie.apk ~/play/shelfie/shelfie.apk1
cd ~/play/shelfie/
mv shelfie.apk1 shelfie.apk
