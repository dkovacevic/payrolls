FROM maven:3-eclipse-temurin-11 AS build

WORKDIR /app
COPY src/ src/
RUN mvn package

FROM eclipse-temurin:11-jre AS runtime
WORKDIR /app
COPY paylocity.yaml /app/paylocity.yaml
COPY --from=build /app/target/paylocity.jar /app/paylocity.jar

CMD ["java", "-jar", "/app/paylocity.jar", "server", "/app/paylocity.yaml"]
