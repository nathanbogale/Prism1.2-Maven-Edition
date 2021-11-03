@rem
@rem Copyright 2015 the original author or authors.
@rem
@rem Licensed under the Apache License, Version 2.0 (the "License");
@rem you may not use this file except in compliance with the License.
@rem You may obtain a copy of the License at
@rem
@rem      https://www.apache.org/licenses/LICENSE-2.0
@rem
@rem Unless required by applicable law or agreed to in writing, software
@rem distributed under the License is distributed on an "AS IS" BASIS,
@rem WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
@rem See the License for the specific language governing permissions and
@rem limitations under the License.
@rem

@if "%DEBUG%" == "" @echo off
@rem ##########################################################################
@rem
@rem  prism-sdk-kotlin-example startup script for Windows
@rem
@rem ##########################################################################

@rem Set local scope for the variables with windows NT shell
if "%OS%"=="Windows_NT" setlocal

set DIRNAME=%~dp0
if "%DIRNAME%" == "" set DIRNAME=.
set APP_BASE_NAME=%~n0
set APP_HOME=%DIRNAME%..

@rem Resolve any "." and ".." in APP_HOME to make it shorter.
for %%i in ("%APP_HOME%") do set APP_HOME=%%~fi

@rem Add default JVM options here. You can also use JAVA_OPTS and PRISM_SDK_KOTLIN_EXAMPLE_OPTS to pass JVM options to this script.
set DEFAULT_JVM_OPTS=

@rem Find java.exe
if defined JAVA_HOME goto findJavaFromJavaHome

set JAVA_EXE=java.exe
%JAVA_EXE% -version >NUL 2>&1
if "%ERRORLEVEL%" == "0" goto execute

echo.
echo ERROR: JAVA_HOME is not set and no 'java' command could be found in your PATH.
echo.
echo Please set the JAVA_HOME variable in your environment to match the
echo location of your Java installation.

goto fail

:findJavaFromJavaHome
set JAVA_HOME=%JAVA_HOME:"=%
set JAVA_EXE=%JAVA_HOME%/bin/java.exe

if exist "%JAVA_EXE%" goto execute

echo.
echo ERROR: JAVA_HOME is set to an invalid directory: %JAVA_HOME%
echo.
echo Please set the JAVA_HOME variable in your environment to match the
echo location of your Java installation.

goto fail

:execute
@rem Setup the command line

set CLASSPATH=%APP_HOME%\lib\prism-sdk-kotlin-example-1.0-SNAPSHOT.jar;%APP_HOME%\lib\prism-api-jvm-1.2.0.jar;%APP_HOME%\lib\prism-credentials-jvm-1.2.0.jar;%APP_HOME%\lib\prism-identity-jvm-1.2.0.jar;%APP_HOME%\lib\prism-crypto-jvm-1.2.0.jar;%APP_HOME%\lib\prism-protos-jvm-1.2.0.jar;%APP_HOME%\lib\kotlinx-coroutines-jdk8-1.5.0.jar;%APP_HOME%\lib\uuid-jvm-0.3.0.jar;%APP_HOME%\lib\prism-common-jvm-1.2.0.jar;%APP_HOME%\lib\bignum-jvm-0.3.1.jar;%APP_HOME%\lib\grpc-kotlin-stub-1.0.0.jar;%APP_HOME%\lib\kotlinx-coroutines-core-jvm-1.5.0.jar;%APP_HOME%\lib\krypto-jvm-2.0.6.jar;%APP_HOME%\lib\kotlin-stdlib-jdk8-1.5.30.jar;%APP_HOME%\lib\bcprov-jdk15on-1.68.jar;%APP_HOME%\lib\runtime-jvm-0.20.5.jar;%APP_HOME%\lib\kotlin-stdlib-jdk7-1.5.30.jar;%APP_HOME%\lib\kotlinx-datetime-jvm-0.2.1.jar;%APP_HOME%\lib\better-parse-jvm-0.4.2.jar;%APP_HOME%\lib\kotlinx-serialization-json-jvm-1.2.2.jar;%APP_HOME%\lib\pbandk-protos-0.20.5.jar;%APP_HOME%\lib\kotlinx-serialization-core-jvm-1.2.2.jar;%APP_HOME%\lib\kotlin-stdlib-1.5.30.jar;%APP_HOME%\lib\kotlin-stdlib-common-1.5.30.jar;%APP_HOME%\lib\prov-1.58.0.0.jar;%APP_HOME%\lib\bitcoinj-core-0.15.10.jar;%APP_HOME%\lib\grpc-okhttp-1.36.0.jar;%APP_HOME%\lib\grpc-protobuf-1.29.0.jar;%APP_HOME%\lib\grpc-protobuf-lite-1.36.0.jar;%APP_HOME%\lib\grpc-core-1.36.0.jar;%APP_HOME%\lib\grpc-stub-1.29.0.jar;%APP_HOME%\lib\grpc-api-1.36.0.jar;%APP_HOME%\lib\guava-30.1-jre.jar;%APP_HOME%\lib\annotations-13.0.jar;%APP_HOME%\lib\core-1.58.0.0.jar;%APP_HOME%\lib\junit-4.12.jar;%APP_HOME%\lib\bcprov-jdk15to18-1.68.jar;%APP_HOME%\lib\protobuf-java-3.11.0.jar;%APP_HOME%\lib\okhttp-3.12.8.jar;%APP_HOME%\lib\slf4j-api-1.7.30.jar;%APP_HOME%\lib\jcip-annotations-1.0.jar;%APP_HOME%\lib\failureaccess-1.0.1.jar;%APP_HOME%\lib\listenablefuture-9999.0-empty-to-avoid-conflict-with-guava.jar;%APP_HOME%\lib\jsr305-3.0.2.jar;%APP_HOME%\lib\checker-qual-3.5.0.jar;%APP_HOME%\lib\error_prone_annotations-2.4.0.jar;%APP_HOME%\lib\j2objc-annotations-1.3.jar;%APP_HOME%\lib\javax.annotation-api-1.2.jar;%APP_HOME%\lib\okhttp-2.7.4.jar;%APP_HOME%\lib\okio-1.17.5.jar;%APP_HOME%\lib\perfmark-api-0.23.0.jar;%APP_HOME%\lib\animal-sniffer-annotations-1.19.jar;%APP_HOME%\lib\protobuf-javalite-3.12.0.jar;%APP_HOME%\lib\hamcrest-core-1.3.jar;%APP_HOME%\lib\proto-google-common-protos-1.17.0.jar;%APP_HOME%\lib\gson-2.8.6.jar;%APP_HOME%\lib\annotations-4.1.1.4.jar;%APP_HOME%\lib\grpc-context-1.36.0.jar


@rem Execute prism-sdk-kotlin-example
"%JAVA_EXE%" %DEFAULT_JVM_OPTS% %JAVA_OPTS% %PRISM_SDK_KOTLIN_EXAMPLE_OPTS%  -classpath "%CLASSPATH%" io.iohk.atala.prism.example.MainKt %*

:end
@rem End local scope for the variables with windows NT shell
if "%ERRORLEVEL%"=="0" goto mainEnd

:fail
rem Set variable PRISM_SDK_KOTLIN_EXAMPLE_EXIT_CONSOLE if you need the _script_ return code instead of
rem the _cmd.exe /c_ return code!
if  not "" == "%PRISM_SDK_KOTLIN_EXAMPLE_EXIT_CONSOLE%" exit 1
exit /b 1

:mainEnd
if "%OS%"=="Windows_NT" endlocal

:omega
