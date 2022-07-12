<template>
  <div class="m-3">
    <h1 class="text-info my-3">{{ component.name }} - Software Repositories</h1>

    <ComponentTabs
      :component-id="component.id"
      :component-available-data="componentAvailableData"
    />

    <b-card title="Total Software Repositories" class="my-3">
      <b-list-group>
        <b-list-group-item variant="success">
          <span class="display-1">
            <FormattedNumber :value="softwareRepositoryCount" />
          </span>
          software repositor{{ softwareRepositoryCount === 1 ? 'y' : 'ies' }}
        </b-list-group-item>
      </b-list-group>
    </b-card>

    <b-card title="Software Repositories">
      <table
        class="table table-dark table-bordered table-striped mt-2"
        style="width: 100%"
      >
        <thead>
          <tr>
            <th>Scanner</th>
            <th>Type</th>
            <th>URL</th>
            <th>Safe</th>
            <th>Scope</th>
          </tr>
        </thead>
        <tbody>
          <tr
            v-for="(
              softwareRepository, softwareRepositoryIndex
            ) in softwareRepositories"
            :key="softwareRepositoryIndex"
          >
            <td>{{ softwareRepository.scannerId }}</td>
            <td>{{ softwareRepository.type }}</td>
            <td>
              <a :href="softwareRepository.url">
                {{ softwareRepository.url }}
              </a>
            </td>
            <td>{{ softwareRepository.safe }}</td>
            <td>{{ softwareRepository.scope }}</td>
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
import {
  Component,
  SoftwareRepositoriesState,
  SoftwareRepository,
} from '~/types/kronicle-service'
import ComponentTabs from '~/components/ComponentTabs.vue'
import FormattedNumber from '~/components/FormattedNumber.vue'
import {
  ComponentAvailableData,
  fetchComponentAvailableData,
} from '~/src/fetchComponentAvailableData'
import { findComponentState } from '~/src/componentStateUtils'
import { NuxtError } from '~/src/nuxtError'

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
      `${$config.serviceBaseUrl}/v1/components/${route.params.componentId}?stateType=software-repositories&fields=component(id,name,teams,states)`
    )
      .then((res) => res.json())
      .then((json) => json.component as Component | undefined)

    if (!component) {
      throw new NuxtError('Component not found', 404)
    }

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
      title: `Kronicle - ${this.component.name} - Software Repositories`,
    }
  },
  computed: {
    softwareRepositories(): SoftwareRepository[] {
      const softwareRepositories: SoftwareRepositoriesState | undefined =
        findComponentState(this.component, 'software-repositories')
      return softwareRepositories?.softwareRepositories ?? []
    },
    softwareRepositoryCount(): number {
      return this.softwareRepositories.length
    },
  },
})
</script>
