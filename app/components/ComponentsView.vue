<template>
  <div>
    <ComponentFilters toggle-name="Other Filters" :components="components" environment-id-filter-enabled>
      <template #top>
        <b-button :variant="documentedOnly ? 'info' : 'secondary'" class="mr-2 my-1" @click="documentedOnly = true">Documented Components</b-button>
        <b-button :variant="documentedOnly ? 'secondary' : 'info'" class="mr-2 my-1" @click="documentedOnly = false">All Components</b-button>
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
import Vue, {PropType} from 'vue'
import {BButton, BCard} from 'bootstrap-vue'
import {Component} from '~/types/kronicle-service'
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
      const filteredComponents = this.$store.state.componentFilters.filteredComponents as Component[];
      return (this.documentedOnly) ? filteredComponents.filter(it => !it.discovered) : filteredComponents;
    },
    componentCount(): number {
      return this.filteredComponents.length
    },
  },
})
</script>
