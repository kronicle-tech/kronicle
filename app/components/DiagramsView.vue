<template>
  <div>
    <b-card no-body class="my-3">
      <b-list-group>
        <b-list-group-item :variant="countVariant">
          <span :class="countClass">{{ count }}</span>
          Diagram{{ count === 1 ? '' : 's' }}
        </b-list-group-item>
      </b-list-group>
    </b-card>

    <ComponentFilters v-if="components.length > 0" :components="components" />

    <DiagramTable :diagrams="diagrams" />
  </div>
</template>

<script lang="ts">
import Vue, { PropType } from 'vue'
import { BCard, BListGroup, BListGroupItem } from 'bootstrap-vue'
import { Component, Diagram } from '~/types/kronicle-service'
import DiagramTable from '~/components/DiagramTable.vue'

export default Vue.extend({
  components: {
    'b-card': BCard,
    'b-list-group': BListGroup,
    'b-list-group-item': BListGroupItem,
    DiagramTable,
  },
  props: {
    components: {
      type: Array as PropType<Component[]>,
      required: true,
    },
    diagrams: {
      type: Array as PropType<Diagram[]>,
      required: true,
    },
  },
  computed: {
    count(): number {
      return this.diagrams.length
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
