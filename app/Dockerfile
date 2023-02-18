FROM node:16 as build

ARG VERSION
ENV VERSION=$VERSION

WORKDIR /app
COPY . .
RUN npm install -g npm@latest
RUN npm ci --ignore-scripts
RUN npm run build --ignore-scripts
RUN rm -rf node_modules/
RUN npm ci --production --ignore-scripts

FROM gcr.io/distroless/nodejs:16

ARG VERSION
ENV VERSION=$VERSION
ENV HOST=0.0.0.0
ENV PORT=3000

WORKDIR /app
COPY --from=build /app  .

EXPOSE $PORT
# NOTE: This health check config is only used by Docker and Docker Compose. If this config is changed, the health check
# config for platforms like Kuberneters and AWS ECS will need to be changed seperately
HEALTHCHECK --interval=60s --timeout=15s \
  CMD ["/nodejs/bin/node", "bin/healthcheck.js"]

CMD ["node_modules/.bin/nuxt", "start"]
