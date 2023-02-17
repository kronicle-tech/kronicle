FROM eclipse-temurin:17-jdk as build

WORKDIR /app
COPY app.jar health-check.jar ./
COPY plugin-libs/ plugin-libs/
COPY plugins/ plugins/

FROM eclipse-temurin:17-jdk

ARG VERSION
ENV VERSION=$VERSION
ENV SPRING_PROFILES_ACTIVE=container
ENV HOST=0.0.0.0
ENV PORT=8090

# Install AWS CLI for use with Kubernetes plugin when connecting to an AWS EKS cluster
RUN apt-get update && \
    apt-get install -y awscli && \
    apt-get clean

WORKDIR /app
COPY --from=build /app  .

EXPOSE $PORT
HEALTHCHECK --interval=60s --timeout=15s \
  CMD ["java", "-jar", "health-check.jar"]

ENTRYPOINT ["java", "-XX:MaxRAMPercentage=80", "-jar", "app.jar"]
