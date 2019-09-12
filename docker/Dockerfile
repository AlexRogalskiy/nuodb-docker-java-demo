# Alpine Linux with OpenJDK JRE
FROM openjdk:8-jre-alpine
# copy WAR into image
COPY nuodb-docker-java-demo-1.0.0.RELEASE.jar /app.jar
# run application with this command line 
CMD ["/usr/bin/java", "-jar", "/app.jar", "dba", "dba", "192.168.0.222/testdb"]
