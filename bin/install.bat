@echo off

echo [INFO] Install App.

cd %~dp0
cd ..
call mvn clean install -pl . -Dmaven.test.skip=true  

echo [INFO] Install wb-core

cd %~dp0
cd ../wb-core
call mvn clean install -pl . -Dmaven.test.skip=true

echo [INFO] Install wb-web-bbs

cd %~dp0
cd ../wb-web-bbs
call mvn clean install -pl .  -Dmaven.test.skip=true

cd ../bin
pause