FROM gcr.io/distroless/java11-debian11

ARG VERSION
ENV VERSION=$VERSION
ENV SPRING_PROFILES_ACTIVE=container
ENV HOST=0.0.0.0
ENV PORT=8090

WORKDIR /app
COPY app.jar Healthcheck.java ./

EXPOSE $PORT
HEALTHCHECK --interval=60s --timeout=15s \
  CMD ["java", "Healthcheck.java"]

ENTRYPOINT ["java", "-XX:MaxRAMPercentage=80", "-jar", "app.jar"]
