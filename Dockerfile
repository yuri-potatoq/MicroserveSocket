FROM gradle:6.8.1-jdk8 AS build

ENV WORK_DIR=/home/microserve

COPY . $WORK_DIR
WORKDIR $WORK_DIR

RUN gradle build

# Segundo build
FROM openjdk:8

EXPOSE 6060

COPY --from=build ./home/microserve/build/libs  /app

WORKDIR /app

ENTRYPOINT ["java", "-jar", "Microserve-1.0-SNAPSHOT.jar"]
