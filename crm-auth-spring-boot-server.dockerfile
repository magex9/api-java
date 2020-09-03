FROM maven:3-jdk-11 as builder
WORKDIR /build
COPY pom.xml .
COPY . . 
#COPY config ./config
ENV ENVIRONMENT=ut
RUN mvn clean package -DskipTests
#RUN pwd && ls -la
#test

FROM openjdk:11
WORKDIR /application

ENV ENVIRONMENT=ut
COPY --from=builder /build/crm-api-spring-boot-server/target/*-bootable.jar /application/application.jar
COPY --from=builder /build/crm-api-spring-boot-server/config/* /application/

EXPOSE 8080

CMD ["java","-jar","/application/application.jar"]

