<template>
  <div>
    <h1 class="text-info my-3">{{ component.name }} - Software Repositories</h1>
    <ComponentTabs :component-id="component.id" />

    <div class="row">
      <div class="col-sm-6">
        <b-card title="Total Software Repositories">
          <b-list-group>
            <b-list-group-item variant="success">
              <span class="display-1">
                <FormattedNumber :value="softwareRepositoryCount" />
              </span>
              software repositor{{
                softwareRepositoryCount === 1 ? 'y' : 'ies'
              }}
            </b-list-group-item>
          </b-list-group>
        </b-card>
      </div>
      <div class="col-sm-6">
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
                ) in component.softwareRepositories"
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
    </div>
  </div>
</template>

<script lang="ts">
import Vue from 'vue'
import { MetaInfo } from 'vue-meta'
import { BCard, BListGroup, BListGroupItem } from 'bootstrap-vue'
import { Component } from '~/types/kronicle-service'
import ComponentTabs from '~/components/ComponentTabs.vue'
import FormattedNumber from '~/components/FormattedNumber.vue'

export default Vue.extend({
  components: {
    'b-card': BCard,
    'b-list-group': BListGroup,
    'b-list-group-item': BListGroupItem,
    ComponentTabs,
    FormattedNumber,
  },
  async asyncData({ $config, route }) {
    const component = await fetch(
      `${$config.serviceBaseUrl}/v1/components/${route.params.componentId}?fields=component(id,name,softwareRepositories)`
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
  computed: {
    softwareRepositoryCount(): number {
      return this.component.softwareRepositories?.length ?? 0
    },
  },
  head(): MetaInfo {
    return {
      title: `Kronicle - ${this.component.name} - Software Repositories`,
    }
  },
})
</script>
