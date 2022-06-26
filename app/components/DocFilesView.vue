<template>
  <div>
    <b-card no-body class="my-3">
      <b-list-group>
        <b-list-group-item :variant="countVariant">
          <span :class="countClass">{{ count }}</span>
          File{{ count === 1 ? '' : 's' }}
        </b-list-group-item>
      </b-list-group>
    </b-card>

    <DocFileTable :component="component" :doc="doc" />
  </div>
</template>

<script lang="ts">
import Vue, { PropType } from 'vue'
import { BCard, BListGroup, BListGroupItem } from 'bootstrap-vue'
import { Component, DocState } from '~/types/kronicle-service'
import DocFileTable from '~/components/DocFileTable.vue'

export default Vue.extend({
  components: {
    'b-card': BCard,
    'b-list-group': BListGroup,
    'b-list-group-item': BListGroupItem,
    DocFileTable,
  },
  props: {
    component: {
      type: {} as PropType<Component>,
      required: true,
    },
    doc: {
      type: {} as PropType<DocState>,
      required: true,
    },
  },
  computed: {
    count(): number {
      return this.doc.files.length
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
