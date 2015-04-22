FROM java:7
VOLUME /tmp
ADD rest-microservices-store-1.0.1.BUILD-SNAPSHOT.jar /app.jar
RUN bash -c 'touch /app.jar'
ENV RABBITMQ_PORT ${RABBIT_PORT_5672_TCP_PORT}
ENV RABBITMQ_HOST ${RABBIT_PORT_5672_TCP_ADDR}
ENV MONGODB_PORT ${MONGO_PORT_27017_TCP_PORT}
ENV MONGODB_HOST ${MONGO_PORT_27017_TCP_ADDR}
EXPOSE 8081
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/app.jar"]