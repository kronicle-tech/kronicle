<template>
  <div class="m-3">
    <DiagramsView :diagrams="filteredDiagrams" :components="components" />
  </div>
</template>

<script lang="ts">
import Vue from 'vue'
import { MetaInfo } from 'vue-meta'
import { Component, Diagram, GraphState } from '~/types/kronicle-service'
import DiagramsView from '~/components/DiagramsView.vue'

export default Vue.extend({
  components: {
    DiagramsView,
  },
  async asyncData({ $config, route, store }) {
    const components = await fetch(
      `${$config.serviceBaseUrl}/v1/components?fields=components(id,name,typeId,tags,teams,platformId,states(environmentId,pluginId))`
    )
      .then((res) => res.json())
      .then((json) => json.components)

    store.commit('componentFilters/initialize', {
      components,
      route,
    })

    const diagrams = await fetch(
      `${$config.serviceBaseUrl}/v1/diagrams?stateType=graph&fields=diagrams(id,name,description,states(type,nodes(componentId)))`
    )
      .then((res) => res.json())
      .then((json) => json.diagrams)

    return {
      components,
      diagrams,
    }
  },
  data() {
    return {
      components: [] as Component[],
      diagrams: [] as Diagram[],
    }
  },
  head(): MetaInfo {
    return {
      title: 'Kronicle - Diagrams',
    }
  },
  computed: {
    filteredComponents(): Component[] {
      return this.$store.state.componentFilters.filteredComponents
    },
    filteredDiagrams(): Diagram[] {
      const filteredComponentIds = new Set(
        this.filteredComponents.map((component) => component.id)
      )
      return this.diagrams.filter((diagram) =>
        this.diagramIncludesSomeOfComponentIds(diagram, filteredComponentIds)
      )
    },
  },
  methods: {
    getDiagramGraph(diagram: Diagram): GraphState | undefined {
      return diagram.states.find((state) => state.type === 'graph') as
        | GraphState
        | undefined
    },
    diagramIncludesSomeOfComponentIds(
      diagram: Diagram,
      componentIds: ReadonlySet<string>
    ) {
      const graph = this.getDiagramGraph(diagram)
      if (!graph) {
        return false
      }
      return graph.nodes.some((node) => componentIds.has(node.componentId))
    },
  },
})
</script>
