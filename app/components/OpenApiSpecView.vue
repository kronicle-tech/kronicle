<template>
  <div class="openapi-spec"></div>
</template>

<style scoped>
.openapi-spec {
  background-color: #FFF;
}
</style>

<script lang="ts">
import Vue, { PropType } from 'vue'
import {MetaInfo} from "vue-meta";
import { Component, OpenApiSpec } from '~/types/kronicle-service'

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
    this.initRedoc(this.component.openApiSpecs[this.openApiSpecIndex - 1])
  },
  methods: {
    initRedoc(openApiSpec: OpenApiSpec) {
      const windowAny = window as any
      windowAny.Redoc.init(openApiSpec.spec, {}, this.$el)
    },
  },
})
</script>
