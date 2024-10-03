FROM quay.io/keycloak/keycloak:latest

COPY target/keycloak-attributes-verification-1.0.jar /opt/keycloak/providers/

ENTRYPOINT ["/opt/keycloak/bin/kc.sh", "start-dev"]
