import * as nuxt from '@nuxt/types'

export class NuxtError extends Error implements nuxt.NuxtError {
  statusCode: number

  constructor(message: string, statusCode: number) {
    super(message)
    this.statusCode = statusCode
  }
}
