@echo off
echo [INFO] Install Jetty start BBS.

cd %~dp0
cd ../wb-web-bbs/cas-server-webapp
call mvn clean jetty:run  -Dmaven.test.skip=true  

cd ../../bin
pause