FROM adoptopenjdk/openjdk11:alpine-jre
LABEL msm-app="true"

ENV APP_DIR=/opt/app
ENV JAVA_TOOL_OPTIONS="-XX:MaxRAMPercentage=80"
ENV MANAGEMENT_PORT=8081
ENV SPRING_PROFILES_ACTIVE=container

RUN mkdir -p $APP_DIR
COPY app.jar $APP_DIR

HEALTHCHECK --interval=60s --timeout=15s \
  CMD curl --fail "http://localhost:${MANAGEMENT_PORT}/management/health" || exit 1

WORKDIR $APP_DIR

CMD ["/bin/sh", "-c", "java $JAVA_TOOL_OPTIONS -jar $APP_DIR/app.jar"]
