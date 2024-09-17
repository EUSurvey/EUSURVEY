FROM bitnami/tomcat:8.5.100-debian-12-r5

USER root

RUN apt-get update && \
    apt-get install -y fontconfig fonts-dejavu && \
    rm -rf /var/lib/apt/lists/*

COPY ./conf/eulogin.crt eulogin.crt
RUN echo yes | keytool -importcert -alias euloginCertAlias -storepass changeit -file eulogin.crt -cacerts
RUN rm eulogin.crt

RUN rm -rf /opt/bitnami/tomcat/webapps/*
COPY ./dist/eusurvey.war /opt/bitnami/tomcat/webapps/eusurvey.war

USER 1001