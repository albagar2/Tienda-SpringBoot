@echo off
echo Debugging Start > debug_output.txt
echo JAVA_HOME: %JAVA_HOME% >> debug_output.txt
echo Checking Java Version... >> debug_output.txt
java -version 2>> debug_output.txt
echo. >> debug_output.txt
echo Checking Maven Wrapper... >> debug_output.txt
call mvnw.cmd -v >> debug_output.txt 2>&1
echo. >> debug_output.txt
echo Attempting to run Spring Boot... >> debug_output.txt
call mvnw.cmd spring-boot:run >> debug_output.txt 2>&1
