#!/bin/bash

echo "Creating GRAF tarball...."
cp ./dist/graf.jar ./ -v
tar -cvvf graf.tar ./graf.jar GTS* UMLMetamodel Properties.txt Models > /dev/null
bzip2 graf.tar > /dev/null
rm ./graf.jar
echo "Done creating graf.tar.bz2"
