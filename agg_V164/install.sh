#! /bin/bash -f

AGG=agg_V164_classes.jar

RULES=../GTSRules.ggx

export CLASSPATH=$CLASSPATH;$AGG

echo "java -Xmx1000m  agg.gui.AGGAppl $RULES
