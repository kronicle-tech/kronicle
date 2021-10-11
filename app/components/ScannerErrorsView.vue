<template>
  <div>
    <b-container fluid>
      <b-row>
        <b-col>
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
import {BCard, BCardText, BCol, BContainer, BListGroup, BListGroupItem, BRow} from 'bootstrap-vue'
import { Component, ScannerError } from '~/types/kronicle-service'
import ComponentFilters from '~/components/ComponentFilters.vue'
import ScannerErrorTable from '~/components/ScannerErrorTable.vue'

export default Vue.extend({
  components: {
    'b-card': BCard,
    'b-card-text': BCardText,
    'b-col': BCol,
    'b-container': BContainer,
    'b-list-group': BListGroup,
    'b-list-group-item': BListGroupItem,
    'b-row': BRow,
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
