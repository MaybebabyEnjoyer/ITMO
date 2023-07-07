@echo off
if "%~1" == "" (
    echo Usage: %~n0 TEST-CLASS MODE VARIANT?
    exit /b 1
)

set "REPO=%~dp0.."
set "OUT=__OUT"
set "LIB=%REPO%/prolog/lib/*"

set "CLASS=%~1"
set "ARGS=%~2 %~3"

javac ^
    -encoding utf-8 ^
    -d "%OUT%" ^
    "--class-path=%LIB%;%REPO%/prolog;%REPO%/clojure;%REPO%/javascript;%REPO%/java" ^
    "%~dp0%CLASS:.=/%.java" ^
 && java -ea "--class-path=%LIB%;%OUT%" "%CLASS%" %ARGS%
