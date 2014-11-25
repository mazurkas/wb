@echo off

echo [INFO] Make keystore.

keytool -genkey -keystore "D:\localhost.keystore" -alias localhost -keyalg RSA

echo [INFO] Export cer

keytool -export -alias localhost -file D:\localhost.cer -keystore D:\localhost.keystore

echo [INFO] Install wb-web-bbs

keytool -import -alias localhost -file D:\localhost.cer -noprompt -trustcacerts -storetype jks -keystore %JAVA_HOME%\jre\lib\security\cacerts -storepass 123456

pause