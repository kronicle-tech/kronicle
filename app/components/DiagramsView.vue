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

    <DiagramTable :diagrams="diagrams" />
  </div>
</template>

<script lang="ts">
import Vue, { PropType } from 'vue'
import { BCard, BListGroup, BListGroupItem } from 'bootstrap-vue'
import { Diagram } from '~/types/kronicle-service'
import DiagramTable from '~/components/DiagramTable.vue'

export default Vue.extend({
  components: {
    'b-card': BCard,
    'b-list-group': BListGroup,
    'b-list-group-item': BListGroupItem,
    DiagramTable,
  },
  props: {
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
