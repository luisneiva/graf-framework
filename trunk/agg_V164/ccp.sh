#! /bin/bash -f

SCRIPTNAME="ccp.sh"
AGGVersion=agg_V164_classes.jar

export CLASSPATH=$AGGVersion:jgl3.1.0.jar

if [ -z $1 ]; then
	echo "Usage: $SCRIPTNAME [C | D] [nc] [ncc] [o outfile] file"
fi

java -Xmx1000m agg.parser.AGGComputeCriticalPairs $@
