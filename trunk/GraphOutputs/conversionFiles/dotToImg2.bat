@ECHO OFF

:: Runs dot.exe
:: dotToImg [rules,states,<filename.dot>]
:: Alice Farquharson, 12/08/2010

:: Get input from command line, set up defaults
SET format=png
SET toConv=%1

:: If no user input, convert all files
IF NOT DEFINED toConv (SET toConv=all)

IF %toConv%==all (GOTO :CONVA)
IF %toConv%==rules (GOTO :CONVR)
IF %toConv%==states (GOTO :CONVS)
GOTO :CONVF

:: Run dot.exe over the specified files

:CONVA convert all dot files, assume start from this file
for %%X in (..\*.dot) do (dot -Tpng -O "%%X")
GOTO :JOBDONE

:CONVR convert rule files only (best guess), starting from GRAF or this file
for %%X in (GraphOutputs\*Action*.dot) do (dot -Tpng -O "%%X")
for %%X in (..\*Action*.dot) do (dot -Tpng -O "%%X")
for %%X in (GraphOutputs\*Call*.dot) do (dot -Tpng -O "%%X")
for %%X in (..\*Call*.dot) do (dot -Tpng -O "%%X")
GOTO :JOBDONE

:CONVS convert state files only (best guess), assume start from this file
for %%X in (..\state*.dot) do (dot -Tpng -O "%%X")
GOTO :JOBDONE

:CONVF convert a specific file
IF NOT EXIST ..\%toConv% GOTO :E_INVALIDIN
dot -Tpng -O "..\%toConv%"
GOTO :JOBDONE

:JOBDONE
ECHO. Converted %toConv% files to %format%

:CLEANUP
SET format=
SET toConv=
GOTO :EOF (end of file)

:E_INVALIDIN the user input did not match expectations
ECHO. Use 'rules', 'states' or <filename>.dot as an argument. To convert all files, give no argument.
GOTO :CLEANUP

:EOF (end of file)
