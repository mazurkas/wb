@echo off
echo [INFO] Install Jetty start BBS.

cd %~dp0
cd ../wb-web-bbs
call mvn clean jetty:run  -Dmaven.test.skip=true  

cd ../bin
pause