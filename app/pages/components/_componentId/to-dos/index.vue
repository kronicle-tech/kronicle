<template>
  <div>
    <h1 class="text-info my-3">{{ component.name }} - To Dos</h1>
    <ComponentTabs :component-id="component.id" />

    <b-card title="To Dos">
      <b-list-group>
        <b-list-group-item :variant="toDoCountVariant">
          <span :class="toDoCountClass">{{ toDoCount }}</span>
          to do{{ toDoCount === 1 ? '' : 's' }}
        </b-list-group-item>
      </b-list-group>

      <div class="col-sm-6">
        <b-card title="To Dos">
          <table
            class="table table-dark table-bordered table-striped mt-2"
            style="width: 100%"
          >
            <thead>
              <tr>
                <th>File</th>
                <th>Description</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="(toDo, toDoIndex) in component.toDos" :key="toDoIndex">
                <td>{{ toDo.file }}</td>
                <td>{{ toDo.description }}</td>
              </tr>
            </tbody>
          </table>
        </b-card>
      </div>
    </b-card>
  </div>
</template>

<script lang="ts">
import Vue from 'vue'
import { MetaInfo } from 'vue-meta'
import { BCard, BListGroup, BListGroupItem } from 'bootstrap-vue'
import ComponentTabs from '~/components/ComponentTabs.vue'
import { Component } from '~/types/kronicle-service'

export default Vue.extend({
  components: {
    'b-card': BCard,
    'b-list-group': BListGroup,
    'b-list-group-item': BListGroupItem,
    ComponentTabs,
  },
  async asyncData({ $config, route }) {
    const component = await fetch(
      `${$config.serviceBaseUrl}/v1/components/${route.params.componentId}?fields=component(id,name,toDos)`
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
    toDoCount(): number {
      return this.component.toDos?.length ?? 0
    },
    toDoCountVariant(): string {
      return this.toDoCount > 0 ? 'danger' : 'success'
    },
    toDoCountClass(): string {
      return this.toDoCount > 0 ? 'display-1' : ''
    },
  },
  head(): MetaInfo {
    return {
      title: `Kronicle - ${this.component.name} - To Dos`,
    }
  },
})
</script>
