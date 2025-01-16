FROM openjdk:21
EXPOSE 8080
ARG JAR_FILE=Backend/target/structsure-0.0.1-SNAPSHOT.jar
ADD ${JAR_FILE} /home/app.jar
ENTRYPOINT ["java", "-jar", "/home/app.jar"]