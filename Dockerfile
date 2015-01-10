FROM xenia/xenia-ng

MAINTAINER Jakub Marchwicki <kuba.marchwicki@gmail.com>

RUN \
  apt-get update && \
  apt-get install -y openjdk-7-jdk maven && \
  rm -rf /var/lib/apt/lists/*

ENV JAVA_HOME /usr/lib/jvm/java-7-openjdk-amd64

RUN mkdir /xenia-api
EXPOSE 8080
WORKDIR /xenia-api

COPY target/xenia-api-0.0.1-SNAPSHOT.jar /xenia-api/
CMD ["java", "-jar", "xenia-api-0.0.1-SNAPSHOT.jar"]
