FROM maven:3.8.4-openjdk-17 AS build

ENV NEXUS_URL=http://172.21.146.157:8081/repository/maven-releases/
ENV GROUP_ID=com/infinities_x
ENV ARTIFACT_ID=User-app
ENV ARTIFACT_VERSION=1.0-SNAPSHOT
ENV ARTIFACT_PACKAGING=war

WORKDIR /app


COPY pom.xml /app/
COPY src /app/src/


RUN mvn clean package


FROM tomcat:9.0-jdk17-openjdk


COPY --from=build /app/target/${ARTIFACT_ID}-${ARTIFACT_VERSION}.${ARTIFACT_PACKAGING} /usr/local/tomcat/webapps/ROOT.war


EXPOSE 8080


CMD ["catalina.sh", "run"]
