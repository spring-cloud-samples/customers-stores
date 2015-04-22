FROM java:7
VOLUME /tmp
ADD app.jar /app.jar
RUN bash -c 'touch /app.jar'
EXPOSE 9900
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/app.jar"]