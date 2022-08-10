FROM tomcat:9.0.65-jre17
RUN rm -rvf /usr/local/tomcat/webapps/ROOT
ADD  /target/STO_spring-1.0-SNAPSHOT.war /usr/local/tomcat/webapps/ROOT.war
CMD ["catalina.sh", "run"]
EXPOSE 8080