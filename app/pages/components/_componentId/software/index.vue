<template>
  <div class="m-3">
    <h1 class="text-info my-3">{{ component.name }} - Software</h1>

    <ComponentTabs
      :component-id="component.id"
      :component-available-data="componentAvailableData"
    />

    <b-card title="Total Software" class="my-3">
      <b-list-group>
        <b-list-group-item variant="success">
          <span class="display-1">
            <FormattedNumber :value="softwareItemCount" />
          </span>
          software item{{ softwareItemCount === 1 ? '' : 's' }}
        </b-list-group-item>
      </b-list-group>
    </b-card>
    <b-card v-if="softwareItemCount > 0" title="Software">
      <table
        class="table table-dark table-bordered table-striped mt-2"
        style="width: 100%"
      >
        <thead>
          <tr>
            <th>Scanner</th>
            <th>Type</th>
            <th>Dependency Type</th>
            <th>Name</th>
            <th>Version</th>
            <th>Version Selector</th>
            <th>Packaging</th>
            <th>Scope</th>
          </tr>
        </thead>
        <tbody>
          <tr
            v-for="(softwareItem, softwareIndex) in softwareItems"
            :key="softwareIndex"
          >
            <td>{{ softwareItem.scannerId }}</td>
            <td>{{ softwareItem.type }}</td>
            <td>{{ softwareItem.dependencyRelationType }}</td>
            <td>{{ softwareItem.name }}</td>
            <td>{{ softwareItem.version }}</td>
            <td>{{ softwareItem.versionSelector }}</td>
            <td>{{ softwareItem.packaging }}</td>
            <td>{{ softwareItem.scope }}</td>
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
import { Component, Software, SoftwaresState } from '~/types/kronicle-service'
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
      `${$config.serviceBaseUrl}/v1/components/${route.params.componentId}?stateType=softwares&fields=component(id,name,teams,states)`
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
      title: `Kronicle - ${this.component.name} - Software`,
    }
  },
  computed: {
    softwareItems(): Software[] {
      const softwares: SoftwaresState | undefined = findComponentState(
        this.component,
        'softwares'
      )
      if (!softwares) {
        return []
      }
      return softwares.softwares.sort(
        (a: Software, b: Software) =>
          a.scannerId.localeCompare(b.scannerId) ||
          a.name.localeCompare(b.name) ||
          a.version.localeCompare(b.version)
      )
    },
    softwareItemCount(): number {
      return this.softwareItems.length
    },
  },
})
</script>
