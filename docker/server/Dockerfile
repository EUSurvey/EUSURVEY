FROM bitnami/tomcat:8.5.100

USER root
COPY ./conf/eulogin.crt eulogin.crt
RUN echo yes | keytool -importcert -alias euloginCertAlias -storepass changeit -file eulogin.crt -cacerts

RUN rm -rf /opt/bitnami/tomcat/webapps/*
COPY ./dist/eusurvey.war /opt/bitnami/tomcat/webapps/eusurvey.war
COPY ./conf/context.xml  /opt/bitnami/tomcat/conf/context.xml

COPY ./conf/manager-context.xml /opt/bitnami/tomcat/webapps/manager/META-INF/context.xml
USER 1001