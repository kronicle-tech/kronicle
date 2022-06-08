<template>
  <ul v-if="component.openApiSpecs && component.openApiSpecs.length > 0">
    <li
      v-for="(openApiSpec, openApiSpecIndex) in component.openApiSpecs"
      :key="openApiSpecIndex"
    >
      <b-link
        v-if="openApiSpec.spec"
        :href="`/components/${componentId}/openapi-specs/${
          openApiSpecIndex + 1
        }`"
      >
        {{ openApiSpec.url || openApiSpec.file }}
      </b-link>
      <div v-else>
        {{ openApiSpec.url || openApiSpec.file }}
        <b-badge variant="danger">Missing</b-badge>
      </div>
      <Markdown :markdown="openApiSpec.description" />
    </li>
  </ul>
</template>

<script lang="ts">
import Vue, { PropType } from 'vue'
import { BBadge, BLink } from 'bootstrap-vue'
import {
  Component,
  OpenApiSpec,
  OpenApiSpecsState,
} from '~/types/kronicle-service'
import Markdown from '~/components/Markdown.vue'
import { findComponentState } from '~/src/componentStateUtils'

export default Vue.extend({
  components: {
    'b-link': BLink,
    'b-badge': BBadge,
    Markdown,
  },
  props: {
    component: {
      type: Object as PropType<Component>,
      default: undefined,
    },
  },
  computed: {
    openApiSpecs(): OpenApiSpec[] {
      const openApiSpecs: OpenApiSpecsState | undefined = findComponentState(
        this.component,
        'openapi-specs'
      )
      return openApiSpecs?.openApiSpecs ?? []
    },
  },
})
</script>
