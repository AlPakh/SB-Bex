# 1. Сборка приложения
FROM maven:3.9.3-eclipse-temurin-17 AS build

WORKDIR /app
# сначала копируем только pom, чтобы использовать кэш Docker
COPY pom.xml .
RUN mvn dependency:go-offline -B

# теперь копируем исходники и собираем JAR
COPY src ./src
RUN mvn clean package -DskipTests -B

# 2. Минимальный runtime-образ
FROM eclipse-temurin:17-jre-jammy

WORKDIR /app
# берём только готовый JAR из предыдущего шага
COPY --from=build /app/target/Bex-0.0.1-SNAPSHOT.jar ./app.jar

# Открываем порт 8080
EXPOSE 8080

# Запуск приложения
ENTRYPOINT ["java", "-jar", "app.jar"]
