<template>
  <div>
    <div class="main">
      <b-card no-body class="my-3">
        <b-list-group>
          <b-list-group-item :variant="errorCountVariant">
            <span :class="errorCountClass">{{ errorCount }}</span>
            error{{ errorCount === 1 ? '' : 's' }}
          </b-list-group-item>
        </b-list-group>
      </b-card>

      <b-card :title="`${errorCount} Errors`" class="my-3">
        <b-card-text>
          <ScannerErrorTable :components="filteredComponents" />
        </b-card-text>
      </b-card>
    </div>

    <div class="panel">
      <ComponentFilters :components="components" />
    </div>
  </div>
</template>

<style scoped>
.main {
  float: left;
  width: calc(100% - 275px);
}

.panel {
  float: right;
  width: 250px;
}
</style>

<script lang="ts">
import Vue, { PropType } from 'vue'
import { BCard, BCardText, BListGroup, BListGroupItem } from 'bootstrap-vue'
import { Component, ScannerError } from '~/types/component-catalog-service'
import ComponentFilters from '~/components/ComponentFilters.vue'
import ScannerErrorTable from '~/components/ScannerErrorTable.vue'

export default Vue.extend({
  components: {
    'b-card': BCard,
    'b-card-text': BCardText,
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
