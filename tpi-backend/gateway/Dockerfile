# Dockerfile para gateway
FROM eclipse-temurin:21-jre

# Instalar curl para health checks (opcional pero recomendado)
RUN apt-get update && apt-get install -y curl && rm -rf /var/lib/apt/lists/*

WORKDIR /app

# Copiar el JAR de la aplicación
COPY target/*.jar app.jar

# Exponer el puerto que usa la aplicación
EXPOSE 8080

# Health check para verificar que la aplicación está funcionando
HEALTHCHECK --interval=30s --timeout=10s --start-period=40s --retries=3 \
  CMD curl -f http://localhost:8080/actuator/health || exit 1

# Comando para ejecutar la aplicación
ENTRYPOINT ["java", "-jar", "app.jar"]