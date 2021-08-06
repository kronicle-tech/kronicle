import { ServerMiddleware } from '@nuxt/types'

const serverMiddleware: ServerMiddleware = function (_, res, next) {
  res.setHeader('Cross-Origin-Opener-Policy', 'same-origin')
  res.setHeader('Cross-Origin-Embedder-Policy', 'require-corp')
  next()
}

export default serverMiddleware
