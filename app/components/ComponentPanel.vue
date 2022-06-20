<template>
  <div>
    <div v-if="component">
      <h3 class="text-info">{{ component.name }}</h3>
      <b-button class="my-1" :to="`/components/${component.id}`" variant="info">
        More Info
      </b-button>

      <div
        v-for="connectionGroup in connectionGroups"
        :key="connectionGroup.name"
        class="my-4"
      >
        <h5 class="text-info">{{ connectionGroup.name }}</h5>

        <table class="table table-secondary">
          <thead>
            <tr>
              <th>Source</th>
              <th>Label</th>
              <th>Target</th>
            </tr>
          </thead>
          <tbody>
            <template
              v-for="(
                connection, connectionIndex
              ) in connectionGroup.connections"
            >
              <tr :key="connectionIndex">
                <td>
                  <ComponentName
                    :component="{ id: connection.source.componentId }"
                  />
                </td>
                <td>
                  <Markdown :markdown="connection.edge.label" />
                </td>
                <td>
                  <ComponentName
                    :component="{ id: connection.target.componentId }"
                  />
                </td>
              </tr>
              <tr v-if="connection.edge.description" :key="connectionIndex">
                <td colspan="3">
                  <Markdown :markdown="connection.edge.description" />
                </td>
              </tr>
            </template>
          </tbody>
        </table>
      </div>

      <div v-if="component.description" class="my-4">
        <h5 class="text-info">Description</h5>
        <Markdown :markdown="component.description" />
      </div>

      <div v-if="component.notes" class="my-4">
        <h5 class="text-info">Notes</h5>
        <Markdown :markdown="component.notes" />
      </div>

      <div
        v-if="
          component.responsibilities && component.responsibilities.length > 0
        "
        class="my-4"
      >
        <h5 class="text-info">Responsibilities</h5>
        <Responsibilities :responsibilities="component.responsibilities" />
      </div>

      <div v-if="component.teams && component.teams.length > 0" class="my-4">
        <h5 class="text-info">Teams</h5>
        <ComponentTeams :component-teams="component.teams" />
      </div>

      <div v-if="component.tags && component.tags.length > 0" class="my-4">
        <h5 class="text-info">Tags</h5>
        <Tags :tags="component.tags" />
      </div>

      <div v-if="component.links && component.links.length > 0" class="my-4">
        <h5 class="text-info">Links</h5>
        <Links :links="component.links" />
      </div>

      <div
        v-if="component.techDebts && component.techDebts.length > 0"
        class="my-4"
      >
        <h5 class="text-info">Tech Debts</h5>
        <TechDebts :tech-debts="component.techDebts" />
      </div>
    </div>
    <div v-else>
      <h3 class="text-info">Unknown Component</h3>

      <p>
        The software catalog does not contain an entry for
        <span v-if="componentId" class="text-warning">{{ componentId }}</span>
        <span v-else>this component</span>
      </p>
    </div>
  </div>
</template>

<script lang="ts">
import Vue, { PropType } from 'vue'
import { BButton } from 'bootstrap-vue'
import {
  Component,
  Diagram,
  GraphEdge,
  GraphNode,
  GraphState,
} from '~/types/kronicle-service'
import ComponentName from '~/components/ComponentName.vue'
import ComponentTeams from '~/components/ComponentTeams.vue'
import Links from '~/components/Links.vue'
import Markdown from '~/components/Markdown.vue'
import Responsibilities from '~/components/Responsibilities.vue'
import Tags from '~/components/Tags.vue'
import TechDebts from '~/components/TechDebts.vue'

interface Connection {
  source: GraphNode
  target: GraphNode
  edge: GraphEdge
}

interface ConnectionGroup {
  name: string
  connections: Connection[]
}

export default Vue.extend({
  components: {
    'b-button': BButton,
    ComponentName,
    ComponentTeams,
    Links,
    Markdown,
    Responsibilities,
    Tags,
    TechDebts,
  },
  props: {
    component: {
      type: Object as PropType<Component>,
      default: undefined,
    },
    componentId: {
      type: String,
      default: undefined,
    },
    diagram: {
      type: Object as PropType<Diagram>,
      default: undefined,
    },
  },
  computed: {
    graph(): GraphState | undefined {
      if (!this.diagram) {
        return undefined
      }
      return this.diagram.states.find((state) => state.type === 'graph') as
        | GraphState
        | undefined
    },
    connectionGroups(): ConnectionGroup[] {
      if (!this.graph || !this.component) {
        return []
      }
      const nodes = this.graph.nodes
      const connections = this.graph.edges.map((edge) => ({
        source: nodes[edge.sourceIndex],
        target: nodes[edge.targetIndex],
        edge,
      }))
      const componentId = this.component.id
      return [
        {
          name: 'Incoming Connections',
          connections: connections.filter(
            (connection) =>
              connection.source.componentId !== componentId &&
              connection.target.componentId === componentId
          ),
        },
        {
          name: 'Self Connections',
          connections: connections.filter(
            (connection) =>
              connection.source.componentId === componentId &&
              connection.target.componentId === componentId
          ),
        },
        {
          name: 'Outgoing Connections',
          connections: connections.filter(
            (connection) =>
              connection.source.componentId === componentId &&
              connection.target.componentId !== componentId
          ),
        },
      ].filter((connectionGroup) => connectionGroup.connections.length > 0)
    },
  },
})
</script>
