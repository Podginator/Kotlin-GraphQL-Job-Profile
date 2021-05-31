FROM maven:3.5-jdk-8 AS build

RUN mkdir -p /usr/src/app/src
WORKDIR /usr/src/app
ADD pom.xml /usr/src/app
COPY src /usr/src/app/src

ENV JAVA_OPTS='-Xmx1g'
ENV MAVEN_OPTS="-Xmx3000m"

RUN mvn -f /usr/src/app/pom.xml package

FROM java:8-jdk-alpine AS skyworkz-context

COPY --from=build /usr/src/app/target/skyworkz-profile-0.1-jar-with-dependencies.jar /usr/app/skyworkz.jar

RUN apk add --update ca-certificates openssl && update-ca-certificates
RUN wget -O /usr/local/bin/ssm-env https://github.com/remind101/ssm-env/releases/download/v0.0.3/ssm-env
RUN chmod +x /usr/local/bin/ssm-env

ARG AWS_ACCESS_KEY_ID
ARG AWS_SECRET_ACCESS_KEY

ENV API_KEY=ssm:///skyworkz/API_KEY
ENV JWT_KEY=ssm:///skyworkz/JWT_KEY
ENV FIREBASE_CREDS=ssm:///skyworkz/FIREBASE_CREDS
ENV SQL_PASSWORD=ssm:///skyworkz/SQL_PASSWORD
ENV SQL_URL=ssm:///skyworkz/SQL_URL
ENV AWS_REGION=eu-central-1
ENTRYPOINT ["/usr/local/bin/ssm-env", "-with-decryption"]

EXPOSE 7000
CMD ["java","-jar","/usr/app/skyworkz.jar"]
