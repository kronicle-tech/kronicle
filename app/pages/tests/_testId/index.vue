<template>
  <div class="m-3">
    <h1 class="text-info my-3">{{ test.id }} Test</h1>

    <TestView :test="test" :components="components" />
  </div>
</template>

<script lang="ts">
import Vue from 'vue'
import { MetaInfo } from 'vue-meta'
import { Component, Test } from '~/types/kronicle-service'
import TestView from '~/components/TestView.vue'

export default Vue.extend({
  components: {
    TestView,
  },
  async asyncData({ $config, route, store }) {
    const test = await fetch(
      `${$config.serviceBaseUrl}/v1/tests/${route.params.testId}?fields=test(id,description,priority)`
    )
      .then((res) => res.json())
      .then((json) => json.test as Test)

    const components = await fetch(
      `${$config.serviceBaseUrl}/v1/components?fields=components(id,name,typeId,tags,teams,platformId,testResults)`
    )
      .then((res) => res.json())
      .then((json) => json.components as Component[])

    store.commit('componentFilters/initialize', {
      components,
      route,
    })

    return {
      test,
      components,
    }
  },
  data() {
    return {
      test: {} as Test,
      components: [] as Component[],
    }
  },
  head(): MetaInfo {
    return {
      title: `Kronicle - ${this.$route.params.testId} Test`,
    }
  },
})
</script>
