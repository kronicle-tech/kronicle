<template>
  <table v-if="diagrams && diagrams.length > 0" class="table table-dark">
    <thead>
      <tr>
        <th class="diagram">Diagram</th>
        <th class="action">Action</th>
        <th class="description">Description</th>
      </tr>
    </thead>
    <tbody>
      <tr v-for="(diagram, diagramIndex) in diagrams" :key="diagramIndex">
        <td class="diagram">
          <a v-if="diagram" :href="`/diagrams/${diagram.id}`">{{
            diagram.name
          }}</a>
        </td>
        <td class="action table-secondary">
          <b-button :href="`/diagrams/${diagram.id}`" variant="info">
            View Diagram
          </b-button>
        </td>
        <td class="description">
          <Markdown :markdown="diagram.description" />
        </td>
      </tr>
    </tbody>
  </table>
</template>

<script lang="ts">
import Vue, { PropType } from 'vue'
import { BButton } from 'bootstrap-vue'
import { Diagram } from '~/types/kronicle-service'
import Markdown from '~/components/Markdown.vue'

export default Vue.extend({
  components: {
    'b-button': BButton,
    Markdown,
  },
  props: {
    diagrams: {
      type: Array as PropType<Diagram[]>,
      required: true,
    },
  },
})
</script>
