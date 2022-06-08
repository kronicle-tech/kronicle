<template>
  <div>
    <b-card no-body class="my-3">
      <b-list-group>
        <b-list-group-item :variant="errorCountVariant">
          <span :class="errorCountClass">{{ errorCount }}</span>
          error{{ errorCount === 1 ? '' : 's' }}
        </b-list-group-item>
      </b-list-group>
    </b-card>

    <ComponentFilters :components="components" />

    <b-card :title="`${errorCount} Errors`" class="my-3">
      <ScannerErrorTable :components="filteredComponents" />
    </b-card>
  </div>
</template>

<script lang="ts">
import Vue, { PropType } from 'vue'
import { BCard, BListGroup, BListGroupItem } from 'bootstrap-vue'
import { Component, ScannerError } from '~/types/kronicle-service'
import ComponentFilters from '~/components/ComponentFilters.vue'
import ScannerErrorTable from '~/components/ScannerErrorTable.vue'

export default Vue.extend({
  components: {
    'b-card': BCard,
    'b-list-group': BListGroup,
    'b-list-group-item': BListGroupItem,
    ComponentFilters,
    ScannerErrorTable,
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
    errors(): ScannerError[] {
      return this.filteredComponents.flatMap(
        (component) => component.scannerErrors ?? []
      )
    },
    errorCount(): number {
      return this.errors.length
    },
    errorCountVariant(): string {
      return this.errorCount > 0 ? 'danger' : 'success'
    },
    errorCountClass(): string {
      return this.errorCount > 0 ? 'display-1' : ''
    },
  },
})
</script>
