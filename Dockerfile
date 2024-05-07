# Use a base image with Maven and JDK 17 installed
FROM maven:3.8-openjdk-17 AS build

WORKDIR /usr/src/app

COPY pom.xml ./
COPY src ./src/

RUN mvn clean package

FROM openjdk:17

WORKDIR /usr/src/app

COPY --from=build /usr/src/app/target/pollquest-service-1.0-SNAPSHOT-shaded.jar ./

EXPOSE 8008

CMD ["java", "-jar", "pollquest-service-1.0-SNAPSHOT-shaded.jar"]

