#!/bin/sh
mkdir dist/lunatic/
mv dist/lunatic.app dist/lunatic/

hdiutil create -volname lunatic-mac -srcfolder dist/lunatic -ov -format UDZO dist/lunatic-mac.dmg