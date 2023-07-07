@echo off

if "%~2" == "" (
    echo Usage: %~n0 TEST-CLASS MODE VARIANT?
    exit /b 1
)

set "JS=%~dp0"
set "JAVA=%JS%../java"
set "OUT=__OUT"
set "CLASS=%~1"
set "ARGS=%~2 %~3"

if not exist "%OUT%" mkdir "%OUT%"

javac ^
    -encoding utf-8 ^
    -d "%OUT%" ^
    "--class-path=%JS%;%JS%/graal/*;%JAVA%" ^
    "%JS%%CLASS:.=\%.java" ^
  && java -ea "--module-path=%JS%graal" "--class-path=%OUT%" "%CLASS%" %ARGS%
