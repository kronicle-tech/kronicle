<template>
  <div>
    <ComponentFilters
      toggle-name="Other Filters"
      :components="components"
      environment-id-filter-enabled
    >
      <template #top>
        <b-button
          :variant="effectiveDocumentedOnly ? 'info' : 'secondary'"
          class="mr-2 my-1"
          @click="documentedOnly = true"
          >Documented Components Only</b-button
        >
        <b-button
          :variant="!effectiveDocumentedOnly ? 'info' : 'secondary'"
          class="mr-2 my-1"
          @click="documentedOnly = false"
          >All Components</b-button
        >
      </template>
    </ComponentFilters>

    <b-card
      :title="`${componentCount} Component${componentCount === 1 ? '' : 's'}`"
    >
      <ComponentTable :components="filteredComponents" />
    </b-card>
  </div>
</template>

<script lang="ts">
import Vue, { PropType } from 'vue'
import { BButton, BCard } from 'bootstrap-vue'
import { Component } from '~/types/kronicle-service'
import ComponentFilters from '~/components/ComponentFilters.vue'
import ComponentTable from '~/components/ComponentTable.vue'

export default Vue.extend({
  components: {
    'b-button': BButton,
    'b-card': BCard,
    ComponentFilters,
    ComponentTable,
  },
  props: {
    components: {
      type: Array as PropType<Component[]>,
      default: undefined,
    },
  },
  data() {
    return {
      documentedOnly: true,
    }
  },
  computed: {
    filteredComponents(): Component[] {
      const filteredComponents = this.$store.state.componentFilters
        .filteredComponents as Component[]
      return this.documentedOnly &&
        filteredComponents.length === this.components.length
        ? filteredComponents.filter((it) => !it.discovered)
        : filteredComponents
    },
    effectiveDocumentedOnly(): boolean {
      return (
        this.documentedOnly &&
        this.$store.state.componentFilters.filteredComponents.length ===
          this.components.length
      )
    },
    componentCount(): number {
      return this.filteredComponents.length
    },
  },
})
</script>
