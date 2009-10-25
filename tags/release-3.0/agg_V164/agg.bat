@echo off

REM Edit the lines below to adjust to your java home.
set JAVA_HOME=C:\Java\jdk1.6.0

set AGG=agg_V164_classes.jar

set RULES=../GTSRules.ggx

SET CLASSPATH=%JAVA_HOME%\bin;%PATH%;%AGG%

java -version:1.6 -Xmx1000m agg.gui.AGGAppl %RULES%

 
