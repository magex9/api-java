FROM maven:3-jdk-11 as builder
WORKDIR /build
COPY pom.xml .
COPY src ./src
COPY config ./config
ENV ENVIRONMENT=ut
RUN mvn clean package -DskipTests
#test

FROM openjdk:11

ENV ENVIRONMENT=ut
COPY --from=builder /build/target/crm-helloworld-*-bootable.jar /crm-helloworld.jar
COPY --from=builder /build/config/* /

EXPOSE 8080

CMD ["java","-jar","/crm-helloworld.jar"]

