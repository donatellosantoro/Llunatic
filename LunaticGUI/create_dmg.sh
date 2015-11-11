#!/bin/sh
mkdir dist/llunatic/
mv dist/llunatic.app dist/llunatic/

hdiutil create -volname llunatic-mac -srcfolder dist/llunatic -ov -format UDZO dist/llunatic-mac.dmg