FROM library/node:12.20.1-alpine
LABEL msm-app="true"

ENV APP_DIR=/opt/app
ENV PORT=3000
ARG VERSION
ENV VERSION=$VERSION

RUN mkdir -p $APP_DIR
WORKDIR $APP_DIR
COPY . $APP_DIR
RUN npm install
RUN npm run build

EXPOSE $PORT

HEALTHCHECK --interval=60s --timeout=15s \
  CMD curl --fail "http://localhost:$PORT" || exit 1

ENV NUXT_HOST=0.0.0.0
ENV NUXT_PORT=$PORT

CMD ["npm", "start"]
