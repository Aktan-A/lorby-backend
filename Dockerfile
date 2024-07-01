FROM maven:3.9.7-eclipse-temurin-17-focal AS build

WORKDIR /app

COPY pom.xml .
RUN mvn dependency:go-offline

COPY src ./src
RUN mvn package -DskipTests

FROM eclipse-temurin:17-jdk-focal

WORKDIR /app

COPY --from=build /app/target/lorby-0.0.1-SNAPSHOT.jar /app/app.jar

EXPOSE 8080

CMD ["java", "-jar", "-Dspring.profiles.active=default", "/app/app.jar"]
