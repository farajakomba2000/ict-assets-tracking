@echo off
setlocal
set DIRNAME=%~dp0
set DIRNAME=%DIRNAME:~0,-1%
set MAVEN_PROJECTBASEDIR=%DIRNAME%
set MAVEN_WRAPPER_JAR=%MAVEN_PROJECTBASEDIR%\.mvn\wrapper\maven-wrapper.jar
set MAVEN_CMD_LINE_ARGS=%*
set MAVEN_OPTS=-Dmaven.multiModuleProjectDirectory="%MAVEN_PROJECTBASEDIR%"
if defined JAVA_HOME (
  "%JAVA_HOME%\bin\java" %MAVEN_OPTS% -classpath "%MAVEN_WRAPPER_JAR%" org.apache.maven.wrapper.MavenWrapperMain %MAVEN_CMD_LINE_ARGS%
) else (
  java %MAVEN_OPTS% -classpath "%MAVEN_WRAPPER_JAR%" org.apache.maven.wrapper.MavenWrapperMain %MAVEN_CMD_LINE_ARGS%
)
endlocal
