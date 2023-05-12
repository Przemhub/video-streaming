FROM eclipse-temurin:11
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} video-streaming.jar
ENTRYPOINT ["java","-jar","/video-streaming.jar"]

