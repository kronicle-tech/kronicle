<template>
  <div class="openapi-spec"></div>
</template>

<script lang="ts">
import Vue, { PropType } from 'vue'
import { MetaInfo } from 'vue-meta'
import {
  Component,
  OpenApiSpec,
  OpenApiSpecsState,
} from '~/types/kronicle-service'
import { findComponentState } from '~/src/componentStateUtils'

export default Vue.extend({
  props: {
    component: {
      type: Object as PropType<Component>,
      required: true,
    },
    openApiSpecIndex: {
      type: Number,
      required: true,
    },
  },
  head(): MetaInfo {
    return {
      script: [
        {
          src: 'https://cdn.jsdelivr.net/npm/redoc@2.0.0-rc.45/bundles/redoc.standalone.js',
        },
      ],
    }
  },
  mounted() {
    const openApiSpecs: OpenApiSpecsState | undefined = findComponentState(
      this.component,
      'openapi-specs'
    )
    this.load(openApiSpecs!.openApiSpecs[this.openApiSpecIndex - 1])
  },
  methods: {
    load(openApiSpec: OpenApiSpec) {
      const windowAny = window as any
      windowAny.Redoc.init(openApiSpec.spec, {}, this.$el)
    },
  },
})
</script>

<style scoped>
.openapi-spec {
  background-color: #fff;
}
</style>
