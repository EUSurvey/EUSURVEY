FROM tomcat:8.5.41-jdk8

# arguments
# ./conf/eulogin.crt is a trusted certificate for SSO integration
# ./conf/context.xml is the context configuration for tomcat
# ./conf/manager-context.xml is the tomcat's manager context configuration
# ./conf/setenv.sh is the environment variables configuration

COPY ./conf/eulogin.crt eulogin.crt
RUN echo yes | keytool -importcert -alias euloginCertAlias -storepass changeit -file eulogin.crt -keystore "$JAVA_HOME/jre/lib/security/cacerts"

RUN ["rm", "-fr", "/usr/local/tomcat/webapps/ROOT"]
COPY ./dist/eusurvey.war /usr/local/tomcat/webapps/eusurvey.war
COPY ./conf/setenv.sh /usr/local/tomcat/bin/setenv.sh
COPY ./conf/context.xml /usr/local/tomcat/conf/context.xml
COPY ./conf/manager-context.xml /usr/local/tomcat/webapps/manager/META-INF/context.xml