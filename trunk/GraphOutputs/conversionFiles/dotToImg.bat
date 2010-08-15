@ECHO OFF

:: Runs dot.exe
:: dotToImg [rules,states]
:: Alice Farquharson, 12/08/2010

:: Check that we have dot.exe in the right place to work with
SET dotPath=%~dp0Graphviz2.26.3/bin/dot.exe
IF NOT EXIST %dotPath% (GOTO :E_NODOT)

:: Get input from command line, set up defaults
SET format=png
SET toConv=%1
IF NOT DEFINED toConv (SET toConv=all) ELSE (IF /i "%toConv%" NEQ "rules" (IF /i "%toConv%" NEQ "states" (GOTO :E_INVALIDINPUT)))

IF %toConv%==all (GOTO :CONVA)
IF %toConv%==rules (GOTO :CONVR)
IF %toConv%==states (GOTO :CONVS)

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

:JOBDONE
ECHO. Converted %toConv% files to %format%

:CLEANUP
SET format=
SET toConv=
SET dotPath=
GOTO :EOF (end of file)

:E_NODOT dot program not found
ECHO. dot.exe not found!
GOTO :CLEANUP

:E_INVALIDINPUT the user input did not match expectations
ECHO. Please use 'rules' or 'states' as an argument. To convert all files, give no argument.
GOTO :CLEANUP

:EOF (end of file)
