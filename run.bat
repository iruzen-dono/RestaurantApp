@echo off
REM Script de compilation et execution de RestaurantApp

setlocal enabledelayedexpansion

echo.
echo ========================================
echo    RestaurantApp - Script de Lancement
echo ========================================
echo.

REM Verifier si Java est installe
java -version >nul 2>&1
if errorlevel 1 (
    echo ERREUR: Java n'est pas installe ou non accessible
    pause
    exit /b 1
)

REM Creer le dossier bin s'il n'existe pas
if not exist "bin" (
    echo Creation du dossier bin...
    mkdir bin
)

REM Compiler tous les fichiers Java
echo Compilation en cours...
javac -cp "lib/*;src" -d bin src\Main.java src\models\*.java src\dao\*.java src\ui\frames\*.java src\ui\panels\*.java src\utils\*.java 2>error.log

if errorlevel 1 (
    echo.
    echo ERREUR: Compilation echouee!
    type error.log
    pause
    exit /b 1
)

echo OK - Compilation reussie

REM Verifier si le jar MySQL est present
if not exist "lib\mysql*.jar" (
    echo.
    echo ATTENTION: Le fichier mysql-connector-java.jar n'a pas ete trouve dans lib/
    echo Veuillez telecharger et placer le jar dans le dossier lib/
    echo.
    pause
    exit /b 1
)

REM Executer l'application
echo.
echo Demarrage de RestaurantApp...
echo.
java -cp "bin;lib/*" Main
