<template>
  <div class="m-3">
    <h1 class="text-info my-3">{{ component.name }} - README</h1>

    <ComponentTabs
      :component-id="component.id"
      :component-available-data="componentAvailableData"
    />

    <b-card v-if="readme" :title="readme.fileName">
      <ReadmeContent :readme="readme" />
    </b-card>

    <b-card v-else title="No README">
      <b-card-text>This component's repo has no README</b-card-text>
    </b-card>
  </div>
</template>

<script lang="ts">
import Vue from 'vue'
import { MetaInfo } from 'vue-meta'
import { BCard, BCardText } from 'bootstrap-vue'
import { Component, ReadmeState } from '~/types/kronicle-service'
import ComponentTabs from '~/components/ComponentTabs.vue'
import ReadmeContent from '~/components/ReadmeContent.vue'
import {
  ComponentAvailableData,
  fetchComponentAvailableData,
} from '~/src/fetchComponentAvailableData'
import { findComponentState } from '~/src/componentStateUtils'

export default Vue.extend({
  components: {
    'b-card': BCard,
    'b-card-text': BCardText,
    ComponentTabs,
    ReadmeContent,
  },
  async asyncData({ $config, route }) {
    const componentAvailableData = await fetchComponentAvailableData(
      $config,
      route
    )

    const component = await fetch(
      `${$config.serviceBaseUrl}/v1/components/${route.params.componentId}?stateType=readme&fields=component(id,name,teams,states)`
    )
      .then((res) => res.json())
      .then((json) => json.component as Component)

    return {
      componentAvailableData,
      component,
    }
  },
  data() {
    return {
      componentAvailableData: {} as ComponentAvailableData,
      component: {} as Component,
    }
  },
  head(): MetaInfo {
    return {
      title: `Kronicle - ${this.component.name} - README`,
    }
  },
  computed: {
    readme(): ReadmeState | undefined {
      return findComponentState(this.component, 'readme')
    },
  },
})
</script>
