#!/bin/bash

echo "Creating GRAF tarball...."
tar -cvvf graf.tar ./dist/graf.jar GTS* UMLMetamodel Properties.txt Models > /dev/null
bzip2 graf.tar > /dev/null
echo "Done creating graf.tar.bz2"
