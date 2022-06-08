<template>
  <div class="m-3">
    <h1 class="text-info my-3">{{ component.name }}</h1>

    <ComponentTabs
      :component-id="component.id"
      :component-available-data="componentAvailableData"
    />

    <b-card-group columns>
      <b-card title="Component Name">
        <b-card-text>
          {{ component.name }}
        </b-card-text>
      </b-card>

      <b-card title="Component Type">
        <b-card-text>
          {{ component.typeId }}
        </b-card-text>
      </b-card>

      <b-card title="Platform">
        <b-card-text>
          {{ component.platformId }}
        </b-card-text>
      </b-card>

      <b-card v-if="component.tags && component.tags > 0" title="Tags">
        <Links :tags="component.tags" />
      </b-card>

      <b-card
        v-if="component.teams && component.teams.length > 0"
        title="Teams"
      >
        <ComponentTeams :component-teams="component.teams" />
      </b-card>

      <b-card v-if="keySoftwares" title="Key Software">
        <KeySoftwareBadges :key-software="keySoftwares" />
      </b-card>

      <b-card v-if="component.description" title="Description">
        <Markdown :markdown="component.description" />
      </b-card>

      <b-card v-if="component.notes" title="Notes">
        <Markdown :markdown="component.notes" :toc="true" />
      </b-card>

      <b-card
        v-if="component.links && component.links.length > 0"
        title="Links"
      >
        <Links :links="component.links" />
      </b-card>

      <b-card
        v-if="
          component.responsibilities && component.responsibilities.length > 0
        "
        title="Responsibilities"
      >
        <Responsibilities :responsibilities="component.responsibilities" />
      </b-card>
    </b-card-group>
  </div>
</template>

<script lang="ts">
import Vue from 'vue'
import { MetaInfo } from 'vue-meta'
import { BCard, BCardGroup, BCardText } from 'bootstrap-vue'
import {
  Component,
  KeySoftware,
  KeySoftwaresState,
} from '~/types/kronicle-service'
import ComponentTabs from '~/components/ComponentTabs.vue'
import KeySoftwareBadges from '~/components/KeySoftwareBadges.vue'
import Links from '~/components/Links.vue'
import Markdown from '~/components/Markdown.vue'
import ComponentTeams from '~/components/ComponentTeams.vue'
import Responsibilities from '~/components/Responsibilities.vue'
import { fetchComponentAvailableData } from '~/src/fetchComponentAvailableData'
import { findComponentState } from '~/src/componentStateUtils'

export default Vue.extend({
  components: {
    'b-card': BCard,
    'b-card-group': BCardGroup,
    'b-card-text': BCardText,
    ComponentTabs,
    KeySoftwareBadges,
    Links,
    Markdown,
    ComponentTeams,
    Responsibilities,
  },
  async asyncData({ $config, route }) {
    const componentAvailableData = await fetchComponentAvailableData(
      $config,
      route
    )

    const component = await fetch(
      `${$config.serviceBaseUrl}/v1/components/${route.params.componentId}?stateType=key-softwares&fields=component(id,name,typeId,platformId,tags,teams,links,description,notes,responsibilities,states)`
    )
      .then((res) => res.json())
      .then((json) => json.component)

    return {
      componentAvailableData,
      component,
    }
  },
  data() {
    return {
      componentAvailableData: [] as string[],
      component: {} as Component,
    }
  },
  head(): MetaInfo {
    return {
      title: `Kronicle - ${this.component.name}`,
    }
  },
  computed: {
    keySoftwares(): KeySoftware[] {
      const keySoftwares: KeySoftwaresState | undefined = findComponentState(
        this.component,
        'key-softwares'
      )
      return keySoftwares?.keySoftwares ?? []
    },
  },
})
</script>
