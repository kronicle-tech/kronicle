import { format, transports } from 'winston'
const { combine, timestamp, json } = format
const customFields = format((info) => {
  info.logsource = process.env.FQN
  info.appName = 'kronicle-app'
  info.appVersion = process.env.VERSION
  return info
})

const serverSideServiceBaseUrl = process.env.SERVER_SIDE_SERVICE_BASE_URL || 'http://localhost:8090';

export default {
  // Global page headers: https://go.nuxtjs.dev/config-head
  head() {
    const style = []
    if (process.env.CSS_OVERRIDES !== undefined) {
      style.push({
        cssText: process.env.CSS_OVERRIDES,
        type: 'text/css',
      })
    }
    const script = []
    if (process.env.ANALYTICS_PLAUSIBLE_ENABLED === 'true') {
      script.push({
        src: process.env.ANALYTICS_PLAUSIBLE_SCRIPT_URL ?? 'https://plausible.io/js/plausible.js',
        defer: true,
        'data-domain': process.env.ANALYTICS_PLAUSIBLE_DATA_DOMAIN,
      })
    }
    return {
      htmlAttrs: {
        lang: 'en'
      },
      meta: [
        {charset: 'utf-8'},
        {name: 'viewport', content: 'width=device-width, initial-scale=1'},
        {hid: 'description', name: 'description', content: ''},
      ],
      link: [
        {rel: 'icon', type: 'image/x-icon', href: '/favicon.ico'},
        {
          rel: 'stylesheet',
          href: 'https://cdnjs.cloudflare.com/ajax/libs/bootswatch/4.5.3/darkly/bootstrap.min.css',
          integrity:
            'sha512-U4WaRm7u3LeQy69FgQcz1CBxA32VsI/OeUdcCC5iBbwdjbfRcE+9E2wnJjXPO/bRfrClPTDYTLgBOekcTiBEgQ==',
          crossorigin: 'anonymous',
        },
        {
          rel: 'stylesheet',
          href: 'https://cdnjs.cloudflare.com/ajax/libs/highlight.js/10.3.2/styles/darcula.min.css',
          integrity:
            'sha512-0+Gq7jQLhuoMdL8EednGo8delKMhKim1t3XrvVGTqbJPfyv5f4HUJ0DTEN+3E+aM4RGEEfmVJOiomnP9olm4iw==',
          crossorigin: 'anonymous',
        },
      ],
      style,
      script,
    }
  },

  // Global CSS: https://go.nuxtjs.dev/config-css
  css: [
  ],

  // Plugins to run before rendering page: https://go.nuxtjs.dev/config-plugins
  plugins: [
  ],

  // Auto import components: https://go.nuxtjs.dev/config-components
  components: true,

  // Modules for dev and build (recommended): https://go.nuxtjs.dev/config-modules
  buildModules: [
    // https://go.nuxtjs.dev/typescript
    '@nuxt/typescript-build',
    // https://go.nuxtjs.dev/stylelint
    '@nuxtjs/stylelint-module',
  ],

  // Modules: https://go.nuxtjs.dev/config-modules
  modules: [
    // https://go.nuxtjs.dev/bootstrap
    ['bootstrap-vue/nuxt', { css: false }],
    '@nuxtjs/redirect-module',
    '@nuxtjs/proxy',
    'nuxt-healthcheck',
    'nuxt-winston-log',
  ],

  // Build Configuration: https://go.nuxtjs.dev/config-build
  build: {
    extend(config, { isClient }) {
      if (isClient) {
        config.devtool = 'source-map'
      }
    },
  },

  redirect: [{ from: '^/components/?$', to: '/all-components' }],

  proxy: {
    '/api': {
      target: serverSideServiceBaseUrl,
      pathRewrite: {
        '^/api($|/)': '/',
      },
    },
  },

  privateRuntimeConfig: {
    serviceBaseUrl: serverSideServiceBaseUrl,
  },

  publicRuntimeConfig: {
    serviceBaseUrl: '/api',
    version: process.env.VERSION,
    messageMarkdown: process.env.MESSAGE_MARKDOWN,
    messageVariant: process.env.MESSAGE_VARIANT,
    introTitle: process.env.INTRO_TITLE,
    introMarkdown: process.env.INTRO_MARKDOWN,
  },

  winstonLog: {
    useDefaultLogger: false,
    skipRequestMiddlewareHandler: true,
    loggerOptions: {
      format: combine(customFields(), json(), timestamp()),
      transports: [new transports.Console()],
    },
  },

  serverMiddleware: ['~/api/responseHeaders.ts'],
}
