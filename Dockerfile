FROM eclipse-temurin:21-jdk AS build
WORKDIR /app

COPY .mvn/ .mvn/
COPY mvnw pom.xml ./
RUN chmod +x mvnw

COPY src/ src/
RUN ./mvnw -B clean verify

FROM eclipse-temurin:21-jre
WORKDIR /app

COPY --from=build /app/target/webshop-gateway-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 9003
ENTRYPOINT ["java", "-jar", "app.jar"]