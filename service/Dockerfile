FROM eclipse-temurin:11 as build

WORKDIR /app
COPY app.jar Healthcheck.java ./
COPY plugin-libs/ plugin-libs/
COPY plugins/ plugins/

FROM eclipse-temurin:11

ARG VERSION
ENV VERSION=$VERSION
ENV SPRING_PROFILES_ACTIVE=container
ENV HOST=0.0.0.0
ENV PORT=8090

# Install AWS CLI for use with Kubernetes plugin when connecting to an AWS EKS cluster
RUN apt-get update && \
    apt-get install -y unzip && \
    apt-get clean && \
    curl "https://awscli.amazonaws.com/awscli-exe-linux-$(uname -m).zip" -o "awscliv2.zip" && \
    unzip awscliv2.zip && \
    ./aws/install && \
    rm -rf awscliv2.zip && \
    apt-get -y purge curl && \
    apt-get -y purge unzip

WORKDIR /app
COPY --from=build /app  .

EXPOSE $PORT
HEALTHCHECK --interval=60s --timeout=15s \
  CMD ["java", "Healthcheck.java"]

ENTRYPOINT ["java", "-XX:MaxRAMPercentage=80", "-jar", "app.jar"]
