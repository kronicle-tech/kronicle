<template>
  <table v-if="docFiles.length > 0" class="table table-dark">
    <thead>
      <tr>
        <th class="file">File</th>
        <th class="action">Action</th>
      </tr>
    </thead>
    <tbody>
      <tr v-for="(docFile, docFileIndex) in docFiles" :key="docFileIndex">
        <td class="doc">
          <a
            v-if="doc"
            :href="`/components/${component.id}/docs/${doc.id}/files/${docFile.path}`"
            >{{ docFile.path }}</a
          >
        </td>
        <td class="action table-secondary">
          <b-button
            :href="`/components/${component.id}/docs/${doc.id}/files/${docFile.path}`"
            variant="info"
          >
            View File
          </b-button>
        </td>
      </tr>
    </tbody>
  </table>
</template>

<script lang="ts">
import Vue, { PropType } from 'vue'
import { BButton } from 'bootstrap-vue'
import { Component, DocFile, DocState } from '~/types/kronicle-service'
import { compareDocFiles } from '~/src/docFileComparator'

export default Vue.extend({
  components: {
    'b-button': BButton,
  },
  props: {
    component: {
      type: Object as PropType<Component>,
      required: true,
    },
    doc: {
      type: Object as PropType<DocState>,
      required: true,
    },
  },
  computed: {
    docFiles(): DocFile[] {
      return [...this.doc.files].sort(compareDocFiles)
    },
  },
})
</script>
