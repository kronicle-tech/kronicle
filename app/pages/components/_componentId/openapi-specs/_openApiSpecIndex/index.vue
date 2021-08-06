<template>
  <div id="openapi-spec"></div>
</template>

<script lang="ts">
import Vue from 'vue'
import { MetaInfo } from 'vue-meta'
import { OpenApiSpec } from '~/types/component-catalog-service'

export default Vue.extend({
  layout: 'Redoc',
  data() {
    return {
      openApiSpecIndex: parseInt(this.$route.params.openApiSpecIndex, 10),
    }
  },
  async mounted() {
    const that = this
    this.removeCss()
    const openApiSpec = await fetch(
      `${this.$config.serviceBaseUrl}/v1/components/${that.$route.params.componentId}?fields=component(openApiSpecs)`
    )
      .then((res) => res.json())
      .then(
        (json) =>
          json.component.openApiSpecs[
            parseInt(that.$route.params.openApiSpecIndex, 10) - 1
          ]
      )
    this.initRedoc(openApiSpec)
  },
  updated() {
    this.removeCss()
  },
  methods: {
    removeCss() {
      document.head.querySelectorAll('link').forEach((link) => {
        if (link.href.includes('darkly/bootstrap')) {
          document.head.removeChild(link)
        }
      })
    },
    initRedoc(openApiSpec: OpenApiSpec) {
      const windowAny = window as any
      windowAny.Redoc.init(openApiSpec.spec, {}, this.$el)
    },
  },
  head(): MetaInfo {
    return {
      title: `Component Catalog - Component ${
        this.$route.params.componentId
      } - OpenAPI Spec ${parseInt(this.$route.params.openApiSpecIndex, 10)}`,
      link: [],
      script: [
        {
          src: 'https://cdn.jsdelivr.net/npm/redoc@2.0.0-rc.45/bundles/redoc.standalone.js',
        },
      ],
    }
  },
})
</script>
