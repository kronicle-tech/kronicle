FROM eclipse-temurin:17-jdk as build

WORKDIR /app
COPY app.jar health-check.jar ./
COPY plugin-libs/ plugin-libs/
COPY plugins/ plugins/

FROM eclipse-temurin:17-jre

ARG VERSION
ENV VERSION=$VERSION
ENV SPRING_PROFILES_ACTIVE=container
ENV HOST=0.0.0.0
ENV PORT=8090

# Install AWS CLI for use with Kubernetes plugin when connecting to an AWS EKS cluster
RUN apt-get update && \
  apt-get install -y --no-install-recommends unzip && \
  curl "https://awscli.amazonaws.com/awscli-exe-linux-$(uname -m).zip" -o "awscliv2.zip" && \
  unzip awscliv2.zip && \
  ./aws/install && \
  rm -rf awscliv2.zip && \
  apt-get -y purge unzip && \
  apt-get clean && \
  rm -rf /var/lib/apt/*

WORKDIR /app
COPY --from=build /app  .

EXPOSE $PORT
# NOTE: This health check config is only used by Docker and Docker Compose. If this config is changed, the health check
# config for platforms like Kuberneters and AWS ECS will need to be changed seperately
HEALTHCHECK --interval=60s --timeout=15s \
  CMD ["java", "-jar", "health-check.jar"]

ENTRYPOINT ["java", "-XX:MaxRAMPercentage=80", "-jar", "app.jar"]
