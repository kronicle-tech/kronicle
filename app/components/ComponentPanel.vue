<template>
  <div v-if="component">
    <h4 class="text-info">{{ component.name }}</h4>
    <b-button class="my-1" :to="`/components/${component.id}`">
      More Info
    </b-button>

    <div v-if="component.teams && component.teams.length > 0" class="my-4">
      <h5 class="text-primary">Teams</h5>
      <ComponentTeams :component-teams="component.teams" />
    </div>

    <div v-if="component.tags && component.tags.length > 0" class="my-4">
      <h5 class="text-primary">Tags</h5>
      <Tags :tags="component.tags" />
    </div>

    <div v-if="component.description" class="my-4">
      <h5 class="text-primary">Description</h5>
      <Markdown :markdown="component.description" />
    </div>

    <div v-if="component.notes" class="my-4">
      <h5 class="text-primary">Notes</h5>
      <Markdown :markdown="component.notes" />
    </div>

    <div
      v-if="component.responsibilities && component.responsibilities.length > 0"
      class="my-4"
    >
      <h5 class="text-primary">Responsibilities</h5>
      <Responsibilities :responsibilities="component.responsibilities" />
    </div>

    <div
      v-if="component.techDebts && component.techDebts.length > 0"
      class="my-4"
    >
      <h5 class="text-primary">Tech Debts</h5>
      <TechDebts :tech-debts="component.techDebts" />
    </div>

    <div
      v-if="component.openApiSpecs && component.openApiSpecs.length > 0"
      class="my-4"
    >
      <h5 class="text-primary">OpenAPI Specs</h5>
      <OpenApiSpecs :component="component" />
    </div>

    <div v-if="component.links && component.links.length > 0" class="my-4">
      <h5 class="text-primary">Links</h5>
      <Links :links="component.links" />
    </div>
  </div>
</template>

<script lang="ts">
import Vue, { PropType } from 'vue'
import { BButton } from 'bootstrap-vue'
import { Component } from '~/types/kronicle-service'
import Links from '~/components/Links.vue'
import Markdown from '~/components/Markdown.vue'
import OpenApiSpecs from '~/components/OpenApiSpecs.vue'
import ComponentTeams from '~/components/ComponentTeams.vue'
import Responsibilities from '~/components/Responsibilities.vue'
import Tags from '~/components/Tags.vue'
import TechDebts from '~/components/TechDebts.vue'

export default Vue.extend({
  components: {
    'b-button': BButton,
    Links,
    Markdown,
    OpenApiSpecs,
    ComponentTeams,
    Responsibilities,
    Tags,
    TechDebts,
  },
  props: {
    component: {
      type: Object as PropType<Component>,
      default: undefined,
    },
  },
})
</script>
