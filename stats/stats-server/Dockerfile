FROM amazoncorretto:21
WORKDIR /stats/stats-server
COPY target/*.jar app.jar
ENTRYPOINT ["java","-jar","app.jar"]