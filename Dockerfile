FROM openjdk:25-jdk-slim
RUN groupadd spring && useradd spring -g spring
USER spring:spring
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
USER 0
RUN mkdir -p logs
RUN touch logs/app.log