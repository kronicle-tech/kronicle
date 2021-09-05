<template>
  <div>
    <h1 class="text-info my-3">{{ component.name }} - README</h1>
    <ComponentTabs :component-id="component.id" />

    <b-card v-if="component.readme" :title="component.readme.fileName">
      <ReadmeContent :readme="component.readme" />
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
import { Component } from '~/types/kronicle-service'
import ComponentTabs from '~/components/ComponentTabs.vue'
import ReadmeContent from '~/components/ReadmeContent.vue'

export default Vue.extend({
  components: {
    'b-card': BCard,
    'b-card-text': BCardText,
    ComponentTabs,
    ReadmeContent,
  },
  async asyncData({ $config, route }) {
    const component = await fetch(
      `${$config.serviceBaseUrl}/v1/components/${route.params.componentId}?fields=component(id,name,readme)`
    )
      .then((res) => res.json())
      .then((json) => json.component as Component)

    return {
      component,
    }
  },
  data() {
    return {
      component: {} as Component,
    }
  },
  head(): MetaInfo {
    return {
      title: `Kronicle - ${this.component.name} - README`,
    }
  },
})
</script>
