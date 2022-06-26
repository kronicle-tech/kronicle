<template>
  <table v-if="docs && docs.length > 0" class="table table-dark">
    <thead>
      <tr>
        <th class="doc">Doc</th>
        <th class="action">Action</th>
        <th class="description">Description</th>
      </tr>
    </thead>
    <tbody>
      <tr v-for="(doc, docIndex) in docs" :key="docIndex">
        <td class="doc">
          <a
            v-if="doc"
            :href="`/components/${doc.component.id}/docs/${doc.id}`"
            >{{ doc.name }}</a
          >
        </td>
        <td class="action table-secondary">
          <b-button
            :href="`/components/${doc.component.id}/docs/${doc.id}`"
            variant="info"
          >
            View Doc
          </b-button>
        </td>
        <td class="description">
          <Markdown :markdown="doc.description" />
        </td>
      </tr>
    </tbody>
  </table>
</template>

<script lang="ts">
import Vue, { PropType } from 'vue'
import { BButton } from 'bootstrap-vue'
import { Component, DocState } from '~/types/kronicle-service'
import Markdown from '~/components/Markdown.vue'
import { findComponentStates } from '~/src/componentStateUtils'
import { compareDocs } from '~/src/docComparator'

interface DocStateWithComponent extends DocState {
  component: Component
}

export default Vue.extend({
  components: {
    'b-button': BButton,
    Markdown,
  },
  props: {
    components: {
      type: Array as PropType<Component[]>,
      required: true,
    },
  },
  computed: {
    docs(): DocStateWithComponent[] {
      const that = this
      return that.components
        .flatMap((component) => {
          const docs: ReadonlyArray<DocState> = findComponentStates(
            component,
            'doc'
          )
          return (docs ?? []).map((doc) => {
            return {
              ...doc,
              component,
            } as DocStateWithComponent
          })
        })
        .sort(compareDocs)
    },
  },
})
</script>
