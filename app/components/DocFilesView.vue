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

    <div v-if="count > 0" class="mt-3 mb-4">
      <b-button
        :variant="pagesOnly ? 'info' : 'secondary'"
        class="mr-2 my-1"
        @click="pagesOnly = true"
        >Pages Only</b-button
      >
      <b-button
        :variant="pagesOnly ? 'secondary' : 'info'"
        class="mr-2 my-1"
        @click="pagesOnly = false"
        >All Files</b-button
      >
    </div>

    <b-breadcrumb>
      <b-breadcrumb-item :to="`/components/${component.id}/docs`">
        Docs
      </b-breadcrumb-item>
    </b-breadcrumb>

    <DocFileTable :component="component" :doc="filteredDoc" />
  </div>
</template>

<script lang="ts">
import Vue, { PropType } from 'vue'
import {
  BBreadcrumb,
  BBreadcrumbItem,
  BCard,
  BListGroup,
  BListGroupItem,
} from 'bootstrap-vue'
import { Component, DocState } from '~/types/kronicle-service'
import DocFileTable from '~/components/DocFileTable.vue'

export default Vue.extend({
  components: {
    'b-breadcrumb': BBreadcrumb,
    'b-breadcrumb-item': BBreadcrumbItem,
    'b-card': BCard,
    'b-list-group': BListGroup,
    'b-list-group-item': BListGroupItem,
    DocFileTable,
  },
  props: {
    component: {
      type: Object as PropType<Component>,
      required: true,
    },
    doc: {
      type: Object as PropType<DocState>,
      required: true,
    },
  },
  data() {
    return {
      pagesOnly: true,
    }
  },
  computed: {
    filteredDoc(): DocState {
      const files = this.pagesOnly
        ? this.doc.files.filter((it) => it.contentType === 'text')
        : this.doc.files
      return {
        ...this.doc,
        files,
      }
    },
    count(): number {
      return this.filteredDoc.files.length
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
