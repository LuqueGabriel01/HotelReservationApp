FROM maven:3.9.9-eclipse-temurin-21 AS builder

ARG UID=1010
ARG GID=1010

RUN groupadd -g "${GID}" hotelapp \
    && useradd --create-home --no-log-init -u "${UID}" -g hotelapp hotelapp

WORKDIR /opt/app

COPY --chown=hotelapp:hotelapp pom.xml .
RUN mvn dependency:go-offline -q

COPY --chown=hotelapp:hotelapp src ./src

RUN mvn clean package -DskipTests -q

FROM eclipse-temurin:21-jre AS runtime

ARG BUILD_DATE=unknown
ARG BUILD_VERSION=unknown
ARG IMAGE_DESCRIPTION=unknown
ARG IMAGE_NAME="Hotel App - MS Notification"
ARG UID=1010
ARG GID=1010

LABEL hotelapp-date=$BUILD_DATE \
      hotelapp.name=$IMAGE_NAME \
      hotelapp.description=$IMAGE_DESCRIPTION \
      hotelapp.base.image="eclipse-temurin:21-jre" \
      hotelapp.version=$BUILD_VERSION \
      maintainer="hotelapp"

RUN groupadd -g "${GID}" hotelapp \
    && useradd --create-home --no-log-init -u "${UID}" -g hotelapp hotelapp

WORKDIR /opt/app

COPY --from=builder --chown=hotelapp:hotelapp /opt/app/target/ms-notification.jar app.jar

USER hotelapp

ENTRYPOINT ["java","-jar","app.jar"]