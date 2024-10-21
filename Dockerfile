FROM maven:3.9.9-amazoncorretto-21-debian-bookworm as build
COPY src /usr/src/app/src
COPY pom.xml /usr/src/app
COPY config.properties /usr/src/app
RUN mvn -f /usr/src/app/pom.xml clean package

FROM debian:bookworm-slim
WORKDIR /opt/app
COPY --from=build /usr/src/app/target/owlet.jar .
COPY --from=build /usr/src/app/config.properties .
COPY --from=build /usr/src/app/target/runtime runtime

ENV JAVA_HOME=/opt/app/runtime
ENV PATH="${JAVA_HOME}/bin:${PATH}"

ENTRYPOINT ["java", "-jar", "owlet.jar"]