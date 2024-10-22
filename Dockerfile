ARG KEYCLOAK_VERSION=25.0.2

# Build provider
FROM maven:3-openjdk-17-slim AS builder
ARG KEYCLOAK_VERSION
WORKDIR /app/
COPY pom.xml .
RUN mvn dependency:resolve
COPY src ./src
RUN mvn clean package -Dkeycloak.version=$KEYCLOAK_VERSION

################################################################################

# Base image from official keycloak
FROM quay.io/keycloak/keycloak:$KEYCLOAK_VERSION
COPY --from=builder /app/target/*.jar /opt/keycloak/providers
RUN /opt/keycloak/bin/kc.sh build --db=mysql

