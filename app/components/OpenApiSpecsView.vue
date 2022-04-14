<template>
  <div>
    <b-card no-body>
      <b-list-group>
        <b-list-group-item :variant="countVariant">
          <span :class="countClass">{{ count }}</span>
          OpenAPI spec{{ count === 1 ? '' : 's' }}
        </b-list-group-item>
      </b-list-group>
    </b-card>

    <ComponentFilters :components="components" />

    <OpenApiSpecTable :components="filteredComponents" />
  </div>
</template>

<script lang="ts">
import Vue, { PropType } from 'vue'
import {BCard, BListGroup, BListGroupItem} from 'bootstrap-vue'
import { Component, OpenApiSpec } from '~/types/kronicle-service'
import ComponentFilters from '~/components/ComponentFilters.vue'
import OpenApiSpecTable from '~/components/OpenApiSpecTable.vue'

export default Vue.extend({
  components: {
    'b-card': BCard,
    'b-list-group': BListGroup,
    'b-list-group-item': BListGroupItem,
    ComponentFilters,
    OpenApiSpecTable,
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
    openApiSpecs(): OpenApiSpec[] {
      return this.filteredComponents.flatMap(
        (component) => component.openApiSpecs ?? []
      )
    },
    count(): number {
      return this.openApiSpecs.length
    },
    countVariant(): string {
      return this.count > 0 ? 'success' : 'danger'
    },
    countClass(): string {
      return this.count > 0 ? '' : 'display-1'
    },
  },
})
</script>
