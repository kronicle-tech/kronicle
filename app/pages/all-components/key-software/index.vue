<template>
  <div>
    <AllComponentsTabs />
    <div class="main">
      <table
        class="table table-dark table-bordered table-striped mt-2"
        style="width: 100%"
      >
        <thead>
          <tr>
            <th class="component-type">Type</th>
            <th class="component-name">Name</th>
            <!-- eslint-disable vue/require-v-for-key -->
            <template v-for="keySoftwareName in keySoftwareNames">
              <th>{{ keySoftwareName }}</th>
            </template>
            <!-- eslint-enable -->
          </tr>
        </thead>
        <tbody>
          <tr v-for="component in filteredComponents" :key="component.id">
            <td>{{ component.typeId }}</td>
            <td class="component-name table-primary">
              <ComponentName :component="component" />
            </td>
            <!-- eslint-disable vue/require-v-for-key -->
            <template v-for="keySoftwareName in keySoftwareNames">
              <td>
                <span
                  v-for="(version, versionIndex) in getKeySoftwareVersions(
                    component,
                    keySoftwareName
                  )"
                  :key="versionIndex"
                >
                  {{ version }}<br />
                </span>
              </td>
            </template>
            <!-- eslint-enable -->
          </tr>
        </tbody>
      </table>
    </div>

    <div class="panel">
      <ComponentFilters :components="components" />
    </div>
  </div>
</template>

<style scoped>
.main {
  float: left;
  width: calc(100% - 275px);
}

.panel {
  float: right;
  width: 250px;
}
</style>

<script lang="ts">
import Vue from 'vue'
import { MetaInfo } from 'vue-meta'
import { Component } from '~/types/component-catalog-service'
import AllComponentsTabs from '~/components/AllComponentsTabs.vue'
import ComponentFilters from '~/components/ComponentFilters.vue'
import ComponentName from '~/components/ComponentName.vue'

export default Vue.extend({
  components: {
    AllComponentsTabs,
    ComponentFilters,
    ComponentName,
  },
  async asyncData({ $config, route, store }) {
    const components = await fetch(
      `${$config.serviceBaseUrl}/v1/components?fields=components(id,name,typeId,tags,teams,platformId,keySoftware)`
    )
      .then((res) => res.json())
      .then((json) => json.components as Component[])

    store.commit('componentFilters/initialize', {
      components,
      route,
    })

    return {
      components,
    }
  },
  data() {
    return {
      components: [] as Component[],
    }
  },
  computed: {
    filteredComponents(): Component[] {
      return this.$store.state.componentFilters.filteredComponents
    },
    keySoftwareNames(): string[] | undefined {
      const names = this.filteredComponents
        .map((component) => {
          if (!component.keySoftware) {
            return undefined
          }

          return component.keySoftware.map((keySoftware) => keySoftware.name)
        })
        .flat()
        .sort()
      return Array.from(new Set(names)) as string[]
    },
  },
  methods: {
    getKeySoftwareVersions(component: Component, keySoftwareName: string) {
      const keySoftware = component.keySoftware?.find(
        (keySoftware) => keySoftware.name === keySoftwareName
      )

      return keySoftware ? keySoftware.versions : []
    },
  },
  head(): MetaInfo {
    return {
      title: 'Component Catalog - All Components - Key Software',
    }
  },
})
</script>
