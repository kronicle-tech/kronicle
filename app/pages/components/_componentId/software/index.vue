<template>
  <div>
    <h1 class="text-info my-3">{{ component.name }} - Software</h1>
    <ComponentTabs :component-id="component.id" />

    <div class="row">
      <div class="col-sm-12">
        <b-card title="Total Software">
          <b-list-group>
            <b-list-group-item variant="success">
              <span class="display-1">
                <FormattedNumber :value="softwareCount" />
              </span>
              software item{{ softwareCount === 1 ? '' : 's' }}
            </b-list-group-item>
          </b-list-group>
        </b-card>
      </div>
    </div>
    <div v-if="softwareCount > 0" class="row">
      <div class="col-sm-12">
        <b-card title="Software">
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
                v-for="(software, softwareIndex) in component.software"
                :key="softwareIndex"
              >
                <td>{{ software.scannerId }}</td>
                <td>{{ software.type }}</td>
                <td>{{ software.dependencyType }}</td>
                <td>{{ software.name }}</td>
                <td>{{ software.version }}</td>
                <td>{{ software.versionSelector }}</td>
                <td>{{ software.packaging }}</td>
                <td>{{ software.scope }}</td>
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
import { Component } from '~/types/component-catalog-service'
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
      `${$config.serviceBaseUrl}/v1/components/${route.params.componentId}?fields=component(id,name,software)`
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
    softwareCount(): number {
      return this.component.software?.length ?? 0
    },
  },
  head(): MetaInfo {
    return {
      title: `Component Catalog - ${this.component.name} - Software`,
    }
  },
})
</script>
