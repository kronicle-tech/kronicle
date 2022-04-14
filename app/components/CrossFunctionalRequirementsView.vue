<template>
  <div>
    <ComponentFilters :components="components" />

    <b-card :title="`${count} Cross Functional Requirements`" class="my-3">
      <b-card-text>
        <CrossFunctionalRequirementTable :components="filteredComponents" />
      </b-card-text>
    </b-card>
  </div>
</template>

<script lang="ts">
import Vue, { PropType } from 'vue'
import {BCard, BCardText} from 'bootstrap-vue'
import {
  Component,
  CrossFunctionalRequirement,
} from '~/types/kronicle-service'
import ComponentFilters from '~/components/ComponentFilters.vue'
import CrossFunctionalRequirementTable from '~/components/CrossFunctionalRequirementTable.vue'

export default Vue.extend({
  components: {
    'b-card': BCard,
    'b-card-text': BCardText,
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
