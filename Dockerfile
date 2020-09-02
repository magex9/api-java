FROM maven:3-jdk-11
ARG appname
WORKDIR /build
#COPY pom.xml .
COPY . . 
#COPY config ./config
ENV ENVIRONMENT=ut
RUN mvn clean package -DskipTests
RUN pwd && ls -la

ENV ENVIRONMENT=ut
RUN mkdir -p /application && cp /build/${appname}/target/*-bootable.jar /application/application.jar
RUN cp /build/${appname}/config/* /application/
RUN rm -rf /build

EXPOSE 80

WORKDIR ../application

CMD ["java","-jar","/application/application.jar"]

