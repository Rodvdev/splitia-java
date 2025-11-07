#!/bin/bash

# Script para ejecutar Splitia Backend con Java 17
# Asegura que se use Java 17 en lugar de versiones más recientes

# Configurar JAVA_HOME para Java 17 (macOS con Homebrew)
export JAVA_HOME=/opt/homebrew/opt/openjdk@17/libexec/openjdk.jdk/Contents/Home

# Verificar que Java 17 está disponible
if [ ! -d "$JAVA_HOME" ]; then
    echo "❌ Error: Java 17 no encontrado en $JAVA_HOME"
    echo "Por favor instala Java 17 con: brew install openjdk@17"
    exit 1
fi

# Mostrar versión de Java que se usará
echo "🔧 Usando Java 17 desde: $JAVA_HOME"
$JAVA_HOME/bin/java -version
echo ""

# Ejecutar la aplicación
echo "🚀 Iniciando Splitia Backend..."
echo ""
mvn spring-boot:run

