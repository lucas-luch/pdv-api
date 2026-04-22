FROM eclipse-temurin:21-jdk-alpine AS build

WORKDIR /app

COPY .mvn/ .mvn/
COPY mvnw pom.xml ./

RUN ./mvnw dependency:go-offline -q

COPY src/ src/

RUN ./mvnw package -DskipTests -q

FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

RUN addgroup -S pdv && adduser -S pdv -G pdv

COPY --from=build /app/target/*.jar app.jar

RUN chown pdv:pdv app.jar

USER pdv

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
