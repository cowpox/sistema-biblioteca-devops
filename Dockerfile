# =============================================================
# Dockerfile multi-stage — Sistema Biblioteca
# Estagio 1: build Maven
# Estagio 2: runtime JRE (imagem menor)
# =============================================================

# --- Estagio 1: build ---
FROM eclipse-temurin:17-jdk-alpine AS build
WORKDIR /app

# Copiar apenas arquivos de dependencia primeiro (cache de camadas)
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./

RUN chmod +x mvnw
# Baixar dependencias sem compilar o codigo-fonte
RUN ./mvnw dependency:go-offline -B

# Copiar codigo-fonte e empacotar (sem executar testes — CI ja os executa)
COPY src/ src/
RUN ./mvnw package -DskipTests -B

# --- Estagio 2: runtime ---
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Copiar apenas o fat JAR gerado pelo Spring Boot Maven Plugin
COPY --from=build /app/target/biblioteca-0.0.1-SNAPSHOT.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]
