<template>
  <div class="m-3">
    <h1 class="text-info my-3">{{ component.name }} - {{ doc.name }}</h1>

    <ComponentTabs
      :component-id="component.id"
      :component-available-data="componentAvailableData"
    />

    <DocFilesView :component="component" :doc="doc" />
  </div>
</template>

<script lang="ts">
import Vue from 'vue'
import { MetaInfo } from 'vue-meta'
import { Component, DocState } from '~/types/kronicle-service'
import DocFilesView from '~/components/DocFilesView.vue'
import {
  ComponentAvailableData,
  fetchComponentAvailableData,
} from '~/src/fetchComponentAvailableData'
import { findComponentState } from '~/src/componentStateUtils'

export default Vue.extend({
  components: {
    DocFilesView,
  },
  async asyncData({ $config, route, error }) {
    const componentAvailableData = await fetchComponentAvailableData(
      $config,
      route,
      error
    )

    if (!componentAvailableData) {
      return
    }

    const component = await fetch(
      `${$config.serviceBaseUrl}/v1/components/${route.params.componentId}?stateType=doc&stateId=${route.params.docId}&fields=component(id,name,teams,states)`
    )
      .then((res) => res.json())
      .then((json) => json.component as Component)

    const doc: DocState | undefined = findComponentState(component, 'doc')

    if (!doc) {
      error({
        message: 'Doc not found',
        statusCode: 404,
      })
      return
    }

    return {
      componentAvailableData,
      component,
      doc,
    }
  },
  data() {
    return {
      componentAvailableData: {} as ComponentAvailableData,
      component: {} as Component,
      doc: {} as DocState,
    }
  },
  head(): MetaInfo {
    return {
      title: `Kronicle - ${this.$route.params.componentId} - ${this.$route.params.docId}`,
    }
  },
})
</script>
