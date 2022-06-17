<template>
  <div class="m-3">
    <DiagramsView :diagrams="diagrams" />
  </div>
</template>

<script lang="ts">
import Vue from 'vue'
import { Diagram } from '~/types/kronicle-service'
import DiagramsView from '~/components/DiagramsView.vue'

export default Vue.extend({
  components: {
    DiagramsView,
  },
  async asyncData({ $config }) {
    const diagrams = await fetch(
      `${$config.serviceBaseUrl}/v1/diagrams?fields=diagrams(id,name,type,tags)`
    )
      .then((res) => res.json())
      .then((json) => json.diagrams)

    return {
      diagrams,
    }
  },
  data() {
    return {
      diagrams: [] as Diagram[],
    }
  },
  head() {
    return {
      title: 'Kronicle - Diagrams',
    }
  },
})
</script>
