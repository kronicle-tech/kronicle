FROM gcr.io/distroless/java11-debian11

ARG VERSION
ENV VERSION=$VERSION
ENV SPRING_PROFILES_ACTIVE=container
ENV PORT=8090

WORKDIR /app
COPY app.jar .

HEALTHCHECK --interval=60s --timeout=15s \
  CMD curl --fail "http://localhost:$PORT/health" || exit 1

ENTRYPOINT ["java", "-XX:MaxRAMPercentage=80", "-jar", "app.jar"]
