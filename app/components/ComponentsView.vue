<template>
  <div>
    <b-container fluid>
      <b-row>
        <b-col>
          <b-card
            :title="`${componentCount} Component${componentCount === 1 ? '' : 's'}`"
          >
            <b-card-text>
              <ComponentTable :components="filteredComponents" />
            </b-card-text>
          </b-card>
        </b-col>
        <b-col md="3">
          <ComponentFilters :components="components" />
        </b-col>
      </b-row>
    </b-container>
  </div>
</template>

<script lang="ts">
import Vue, { PropType } from 'vue'
import { BCard, BCardText, BCol, BContainer, BRow } from 'bootstrap-vue'
import { Component } from '~/types/kronicle-service'
import ComponentFilters from '~/components/ComponentFilters.vue'
import ComponentTable from '~/components/ComponentTable.vue'

export default Vue.extend({
  components: {
    'b-card': BCard,
    'b-card-text': BCardText,
    'b-col': BCol,
    'b-container': BContainer,
    'b-row': BRow,
    ComponentFilters,
    ComponentTable,
  },
  props: {
    components: {
      type: Array as PropType<Component[]>,
      default: undefined,
    },
  },
  computed: {
    filteredComponents(): Component[] {
      return this.$store.state.componentFilters.filteredComponents
    },
    componentCount(): number {
      return this.filteredComponents.length
    },
  },
})
</script>
