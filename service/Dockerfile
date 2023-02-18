FROM eclipse-temurin:17-jdk-alpine as build

WORKDIR /app
COPY app.jar health-check.jar ./
COPY plugin-libs/ plugin-libs/
COPY plugins/ plugins/

FROM eclipse-temurin:17-jre-alpine

ARG VERSION
ENV VERSION=$VERSION
ENV SPRING_PROFILES_ACTIVE=container
ENV HOST=0.0.0.0
ENV PORT=8090

# Install AWS CLI for use with Kubernetes plugin when connecting to an AWS EKS cluster
RUN apk add --no-cache aws-cli

WORKDIR /app
COPY --from=build /app  .

EXPOSE $PORT
# NOTE: This health check config is only used by Docker and Docker Compose. If this config is changed, the health check
# config for platforms like Kuberneters and AWS ECS will need to be changed seperately
HEALTHCHECK --interval=60s --timeout=15s \
  CMD ["java", "-jar", "health-check.jar"]

ENTRYPOINT ["java", "-XX:MaxRAMPercentage=80", "-jar", "app.jar"]
