<template>
  <table v-if="components && components.length > 0" class="table table-dark">
    <thead>
      <tr>
        <th class="type">Type</th>
        <th class="name">Name</th>
        <th>Tags</th>
        <th>Teams</th>
        <th>Description</th>
      </tr>
    </thead>
    <tbody>
      <tr
        v-for="(component, componentIndex) in sortedComponents"
        :key="componentIndex"
      >
        <td class="type">{{ component.type }}</td>
        <td class="name table-primary">
          <ComponentName :component="component" />
        </td>
        <td>
          <Tags :tags="component.tags" />
        </td>
        <td>
          <ComponentTeams :component-teams="component.teams" />
        </td>
        <td class="description">
          <Markdown
            v-if="component.description"
            :markdown="component.description"
          />
        </td>
      </tr>
    </tbody>
  </table>
</template>

<script lang="ts">
import Vue, { PropType } from 'vue'
import { compareComponents } from '~/src/componentComparator'
import { Component } from '~/types/kronicle-service'
import ComponentName from '~/components/ComponentName.vue'
import Markdown from '~/components/Markdown.vue'
import Tags from '~/components/Tags.vue'
import ComponentTeams from '~/components/ComponentTeams.vue'

export default Vue.extend({
  components: {
    ComponentName,
    Markdown,
    ComponentTeams,
    Tags,
  },
  props: {
    components: {
      type: Array as PropType<Component[]>,
      default: undefined,
    },
  },
  computed: {
    sortedComponents() {
      return [...this.components].sort(compareComponents)
    },
  },
})
</script>

<style scoped>
.name {
  width: 30%;
}
</style>
