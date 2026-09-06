# Stage 1: Build com Maven e Eclipse Temurin 21
FROM maven:3.9.6-eclipse-temurin-21-alpine AS build
WORKDIR /app

# Baixa as dependências em cache
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copia os fontes e compila o JAR
COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: Runtime leve com JRE 21 Alpine
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

EXPOSE 8080

COPY --from=build /app/target/sso2-0.0.1-SNAPSHOT.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]