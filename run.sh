#!/bin/bash
# Script de compilation et execution de RestaurantApp
# Pour Linux/Mac

echo ""
echo "========================================"
echo "    RestaurantApp - Script de Debug"
echo "========================================"
echo ""

# Verifier si Java est installe
if ! command -v java &> /dev/null; then
    echo "ERREUR: Java n'est pas installe ou non accessible"
    exit 1
fi

# Creer le dossier bin s'il n'existe pas
if [ ! -d "bin" ]; then
    echo "Creer le dossier bin..."
    mkdir -p bin
fi

# Compiler tous les fichiers Java
echo "Compilation en cours..."
javac -cp "lib/*:src" -d bin src/Main.java src/models/*.java src/dao/*.java src/ui/frames/*.java src/ui/panels/*.java src/utils/*.java 2>error.log

if [ $? -ne 0 ]; then
    echo ""
    echo "ERREUR: Compilation echouee!"
    cat error.log
    exit 1
fi

echo "OK - Compilation reussie"

# Verifier si le jar MySQL est present
if [ ! -f lib/mysql*.jar ]; then
    echo ""
    echo "ATTENTION: Le fichier mysql-connector-java.jar n'a pas ete trouve dans lib/"
    echo "Veuillez telecharger et placer le jar dans le dossier lib/"
    echo ""
    exit 1
fi

# Executer l'application
echo ""
echo "Demarrage de RestaurantApp..."
echo ""
java -cp "bin:lib/*" Main
