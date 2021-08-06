<template>
  <div>
    <h1 class="text-info my-3">{{ component.name }}</h1>
    <ComponentTabs :component-id="component.id" />

    <b-card title="Type" class="my-3">
      <b-card-text>
        {{ component.typeId }}
      </b-card-text>
    </b-card>

    <b-card title="Platform" class="my-3">
      <b-card-text>
        {{ component.platformId }}
      </b-card-text>
    </b-card>

    <b-card
      v-if="component.tags && component.tags > 0"
      title="Tags"
      class="my-3"
    >
      <Links :tags="component.tags" />
    </b-card>

    <b-card
      v-if="component.teams && component.teams.length > 0"
      title="Teams"
      class="my-3"
    >
      <ComponentTeams :component-teams="component.teams" />
    </b-card>

    <b-card
      v-if="component.links && component.links.length > 0"
      title="Links"
      class="my-3"
    >
      <Links :links="component.links" />
    </b-card>

    <b-card v-if="component.description" title="Description" class="my-3">
      <Markdown :markdown="component.description" />
    </b-card>

    <b-card v-if="component.notes" title="Notes" class="my-3">
      <Markdown :markdown="component.notes" :toc="true" />
    </b-card>

    <b-card
      v-if="component.responsibilities && component.responsibilities.length > 0"
      title="Responsibilities"
      class="my-3"
    >
      <Responsibilities :responsibilities="component.responsibilities" />
    </b-card>
  </div>
</template>

<script lang="ts">
import Vue from 'vue'
import { MetaInfo } from 'vue-meta'
import { BCard, BCardText } from 'bootstrap-vue'
import { Component } from '~/types/component-catalog-service'
import ComponentTabs from '~/components/ComponentTabs.vue'
import Links from '~/components/Links.vue'
import Markdown from '~/components/Markdown.vue'
import ComponentTeams from '~/components/ComponentTeams.vue'
import Responsibilities from '~/components/Responsibilities.vue'

export default Vue.extend({
  components: {
    'b-card': BCard,
    'b-card-text': BCardText,
    ComponentTabs,
    Links,
    Markdown,
    ComponentTeams,
    Responsibilities,
  },
  async asyncData({ $config, route }) {
    const component = await fetch(
      `${$config.serviceBaseUrl}/v1/components/${route.params.componentId}?fields=component(id,name,typeId,platformId,tags,teams,links,description,notes,responsibilities)`
    )
      .then((res) => res.json())
      .then((json) => json.component)

    return {
      component,
    }
  },
  data() {
    return {
      component: {} as Component,
    }
  },
  head(): MetaInfo {
    return {
      title: `Component Catalog - ${this.component.name}`,
    }
  },
})
</script>
