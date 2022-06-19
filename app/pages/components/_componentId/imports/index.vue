<template>
  <div class="m-3">
    <h1 class="text-info my-3">{{ component.name }} - Imports</h1>

    <ComponentTabs
      :component-id="component.id"
      :component-available-data="componentAvailableData"
    />

    <b-card title="Total Imports" class="my-3">
      <b-list-group>
        <b-list-group-item variant="success">
          <span class="display-1">
            <FormattedNumber :value="importCount" />
          </span>
          import{{ importCount === 1 ? '' : 's' }}
        </b-list-group-item>
      </b-list-group>
    </b-card>

    <b-card title="Imports">
      <table
        class="table table-dark table-bordered table-striped mt-2"
        style="width: 100%"
      >
        <thead>
          <tr>
            <th>Scanner</th>
            <th>Type</th>
            <th>Name</th>
          </tr>
        </thead>
        <tbody>
          <tr
            v-for="(importItem, importItemIndex) in imports"
            :key="importItemIndex"
          >
            <td>{{ importItem.scannerId }}</td>
            <td>{{ importItem.type }}</td>
            <td>{{ importItem.name }}</td>
          </tr>
        </tbody>
      </table>
    </b-card>
  </div>
</template>

<script lang="ts">
import Vue from 'vue'
import { MetaInfo } from 'vue-meta'
import { BCard, BListGroup, BListGroupItem } from 'bootstrap-vue'
import { Component, Import, ImportsState } from '~/types/kronicle-service'
import ComponentTabs from '~/components/ComponentTabs.vue'
import FormattedNumber from '~/components/FormattedNumber.vue'
import {
  ComponentAvailableData,
  fetchComponentAvailableData,
} from '~/src/fetchComponentAvailableData'
import { findComponentState } from '~/src/componentStateUtils'

export default Vue.extend({
  components: {
    'b-card': BCard,
    'b-list-group': BListGroup,
    'b-list-group-item': BListGroupItem,
    ComponentTabs,
    FormattedNumber,
  },
  async asyncData({ $config, route }) {
    const componentAvailableData = await fetchComponentAvailableData(
      $config,
      route
    )

    const component = await fetch(
      `${$config.serviceBaseUrl}/v1/components/${route.params.componentId}?stateType=imports&fields=component(id,name,teams,states)`
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
      title: `Kronicle - ${this.component.name} - Imports`,
    }
  },
  computed: {
    imports(): Import[] {
      const imports: ImportsState | undefined = findComponentState(
        this.component,
        'imports'
      )
      return imports?.imports ?? []
    },
    importCount(): number {
      return this.imports.length
    },
  },
})
</script>
