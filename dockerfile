FROM adoptopenjdk/openjdk11-openj9:jdk-11.0.1.13-alpine-slim
# COPY build/libs/taxii-server-micronaut-*-all.jar taxii-server-micronaut-all.jar
ADD https://github.com/StephenOTT/TAXII-Server/releases/download/v0.5/taxii-server-micronaut-0.5-all.jar taxii-server-micronaut-all.jar
EXPOSE 8080
CMD java -XX:+UnlockExperimentalVMOptions -XX:+UseCGroupMemoryLimitForHeap -Dcom.sun.management.jmxremote -noverify ${JAVA_OPTS} -jar taxii-server-micronaut-all.jar
