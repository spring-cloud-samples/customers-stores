FROM java:7
VOLUME /tmp
ADD rest-microservices-customers-1.0.1.BUILD-SNAPSHOT.jar /app.jar
RUN bash -c 'touch /app.jar'
ENV RABBITMQ_PORT ${RABBIT_PORT_5672_TCP_PORT}
ENV RABBITMQ_HOST ${RABBIT_PORT_5672_TCP_ADDR}
EXPOSE 9000
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/app.jar"]