FROM java:8
LABEL maintainer="kevin.jin01@sap.com"

WORKDIR /barista-service
EXPOSE 8070

ARG JAR_FILE
ADD target/${JAR_FILE} ./barista-service.jar
ENTRYPOINT ["java", "-jar","barista-service.jar"]