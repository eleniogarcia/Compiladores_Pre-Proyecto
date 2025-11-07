@echo off
REM Script de compilación y ejecución del Compilador Mini
REM Universidad Nacional de Villa Mercedes

echo ========================================
echo  Compilador Mini - Script de Build
echo ========================================
echo.

:menu
echo Seleccione una opción:
echo [1] Generar Lexer y Parser
echo [2] Compilar proyecto Java
echo [3] Ejecutar compilador (test.txt)
echo [4] Todo (1+2+3)
echo [5] Ver program.asm
echo [6] Limpiar archivos generados
echo [7] Salir
echo.
set /p opcion="Opción: "

if "%opcion%"=="1" goto generar
if "%opcion%"=="2" goto compilar
if "%opcion%"=="3" goto ejecutar
if "%opcion%"=="4" goto todo
if "%opcion%"=="5" goto ver_asm
if "%opcion%"=="6" goto limpiar
if "%opcion%"=="7" goto salir
goto menu

:generar
echo.
echo [1/2] Generando Lexer con JFlex...
java -jar jflex-full-1.9.1.jar lcalc.flex
if errorlevel 1 (
    echo ERROR: Fallo al generar el Lexer
    pause
    goto menu
)

echo.
echo [2/2] Generando Parser con CUP...
java -jar java-cup-11b.jar -parser parser ycalc.cup
if errorlevel 1 (
    echo ERROR: Fallo al generar el Parser
    pause
    goto menu
)

echo.
echo ✅ Lexer y Parser generados exitosamente
echo.
pause
goto menu

:compilar
echo.
echo Compilando proyecto Java...
javac -cp ".;java-cup-11b-runtime.jar" *.java
if errorlevel 1 (
    echo ERROR: Fallo al compilar el proyecto
    pause
    goto menu
)

echo.
echo ✅ Proyecto compilado exitosamente
echo.
pause
goto menu

:ejecutar
echo.
echo Ejecutando compilador sobre test.txt...
echo.
java -cp ".;java-cup-11b-runtime.jar" Main
if errorlevel 1 (
    echo ERROR: Fallo al ejecutar el compilador
    pause
    goto menu
)

echo.
echo ✅ Compilación completada. Código generado en program.asm
echo.
pause
goto menu

:todo
echo.
echo === Proceso Completo de Build ===
echo.

echo [1/3] Generando Lexer con JFlex...
java -jar jflex-full-1.9.1.jar lcalc.flex
if errorlevel 1 goto error_build

echo [2/3] Generando Parser con CUP...
java -jar java-cup-11b.jar -parser parser ycalc.cup
if errorlevel 1 goto error_build

echo [3/3] Compilando proyecto Java...
javac -cp ".;java-cup-11b-runtime.jar" *.java
if errorlevel 1 goto error_build

echo.
echo === Ejecutando Compilador ===
echo.
java -cp ".;java-cup-11b-runtime.jar" Main

echo.
echo ========================================
echo ✅ TODO COMPLETADO EXITOSAMENTE
echo ========================================
echo.
pause
goto menu

:error_build
echo.
echo ❌ ERROR: El build falló en algún paso
pause
goto menu

:ver_asm
echo.
echo === Contenido de program.asm ===
echo.
if exist program.asm (
    type program.asm
) else (
    echo ERROR: program.asm no existe. Ejecute primero el compilador.
)
echo.
pause
goto menu

:limpiar
echo.
echo Limpiando archivos generados...
del /Q *.class 2>nul
del /Q Lexer.java 2>nul
del /Q parser.java 2>nul
del /Q sym.java 2>nul
del /Q program.asm 2>nul
echo ✅ Archivos limpiados
echo.
pause
goto menu

:salir
echo.
echo Saliendo...
exit /b 0
