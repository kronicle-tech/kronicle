<template>
  <div>
    <b-container fluid>
      <b-row>
        <b-col>
          <b-card no-body>
            <b-list-group>
              <b-list-group-item :variant="countVariant">
                <span :class="countClass">{{ count }}</span>
                OpenAPI spec{{ count === 1 ? '' : 's' }}
              </b-list-group-item>
            </b-list-group>
          </b-card>

          <b-card no-body class="my-3">
            <b-card-text>
              <OpenApiSpecTable :components="filteredComponents" />
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
import { Component, OpenApiSpec } from '~/types/kronicle-service'
import ComponentFilters from '~/components/ComponentFilters.vue'
import OpenApiSpecTable from '~/components/OpenApiSpecTable.vue'

export default Vue.extend({
  components: {
    'b-card': BCard,
    'b-card-text': BCardText,
    'b-list-group': BListGroup,
    'b-list-group-item': BListGroupItem,
    'b-col': BCol,
    'b-container': BContainer,
    'b-row': BRow,
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
