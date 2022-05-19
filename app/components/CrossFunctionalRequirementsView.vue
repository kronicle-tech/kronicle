<template>
  <div>
    <ComponentFilters :components="components" />

    <b-card :title="`${count} Cross Functional Requirements`" class="my-3">
      <CrossFunctionalRequirementTable :components="filteredComponents" />
    </b-card>
  </div>
</template>

<script lang="ts">
import Vue, { PropType } from 'vue'
import {BCard} from 'bootstrap-vue'
import {
  Component,
  CrossFunctionalRequirement,
} from '~/types/kronicle-service'
import ComponentFilters from '~/components/ComponentFilters.vue'
import CrossFunctionalRequirementTable from '~/components/CrossFunctionalRequirementTable.vue'

export default Vue.extend({
  components: {
    'b-card': BCard,
    ComponentFilters,
    CrossFunctionalRequirementTable,
  },
  props: {
    components: {
      type: Array as PropType<Component[]>,
      required: true,
    },
  },
  computed: {
    filteredComponents(): Component[] {
      return this.$store.state.componentFilters.filteredComponents
    },
    crossFunctionalRequirements(): CrossFunctionalRequirement[] {
      return this.filteredComponents.flatMap(
        (component) => component.crossFunctionalRequirements ?? []
      )
    },
    count(): number {
      return this.crossFunctionalRequirements.length
    },
  },
})
</script>
