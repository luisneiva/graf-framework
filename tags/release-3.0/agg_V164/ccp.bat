@echo off

SET SCRIPTNAME="ccp.bat"

REM PLEASE, edit the line below to adjust to your java home.
set JAVA_HOME=C:\Java\jdk1.6.0

set PATH=%JAVA_HOME%\bin;%PATH%

SET AGG=C:\AGG\agg_V164\AGG_V164_CLASSES.JAR

SET JGL=C:\AGG\agg_V164\jgl3.1.0.jar

SET CLASSPATH=%PATH%;%AGG%;%JGL%

ECHO Usage from command line: "%SCRIPTNAME%" [-C] [-D] [-nc] [-ncc] [-o outfile] file

java -Xmx1000m agg.gui.AGGComputeCriticalPairs %1 %2 %3 %4 %5 %6
