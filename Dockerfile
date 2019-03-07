FROM openjdk:8-jre-alpine

ADD ./target/car-demo.jar /app/
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/app/car-demo.jar"]

EXPOSE 8080