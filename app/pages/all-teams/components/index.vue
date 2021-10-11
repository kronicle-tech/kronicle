<template>
  <div>
    <b-container fluid>
      <b-row>
        <b-col>
          <AllTeamsTabs />

          <b-card-group v-for="(row, rowIndex) in rows" :key="rowIndex" deck>
            <b-card
              v-for="(item, itemIndex) in row"
              :key="itemIndex"
              bg-variant="dark"
              :header="item.team.name"
              text-variant="white"
              class="my-3"
            >
              <b-card
                v-for="component in item.components"
                :key="component.id"
                no-body
                class="mt-1"
              >
                <b-card-header header-tag="header" class="p-1" role="tab">
                  <b-button
                    v-b-toggle="`accordion-${rowIndex}-${itemIndex}-${component.id}`"
                    block
                    variant="info"
                  >{{ component.name }}</b-button
                  >
                </b-card-header>
                <b-collapse
                  :id="`accordion-${rowIndex}-${itemIndex}-${component.id}`"
                  visible
                  :accordion="`accordion-${rowIndex}-${itemIndex}`"
                  role="tabpanel"
                >
                  <b-card-body>
                    <ComponentPanel :component="component" />
                  </b-card-body>
                </b-collapse>
              </b-card>
            </b-card>
          </b-card-group>
        </b-col>
      </b-row>
    </b-container>
  </div>
</template>

<script lang="ts">
import Vue from 'vue'
import {
  BButton,
  BCard,
  BCardBody,
  BCardGroup,
  BCardHeader, BCol,
  BCollapse, BContainer, BRow,
  VBToggle,
} from 'bootstrap-vue'
import { MetaInfo } from 'vue-meta'
import { Component, Team } from '~/types/kronicle-service'
import AllTeamsTabs from '~/components/AllTeamsTabs.vue'
import ComponentPanel from '~/components/ComponentPanel.vue'

interface Item {
  team: Team
  components: Component[]
}

export default Vue.extend({
  components: {
    AllTeamsTabs,
    'b-button': BButton,
    'b-card': BCard,
    'b-card-body': BCardBody,
    'b-card-group': BCardGroup,
    'b-card-header': BCardHeader,
    'b-col': BCol,
    'b-collapse': BCollapse,
    'b-container': BContainer,
    'b-row': BRow,
    ComponentPanel,
  },
  directives: {
    'b-toggle': VBToggle,
  },
  async asyncData({ $config }) {
    const teams = await fetch(
      `${$config.serviceBaseUrl}/v1/teams?fields=teams(id,name)`
    )
      .then((res) => res.json())
      .then((json) => json.teams)

    const components = await fetch(
      `${$config.serviceBaseUrl}/v1/components?fields=components(id,name,teams,tags,description,notes,responsibilities,techDebts,openApiSpecs,links)`
    )
      .then((res) => res.json())
      .then((json) => json.components)

    return {
      teams,
      components,
    }
  },
  data() {
    return {
      teams: [] as Team[],
      components: [] as Component[],
    }
  },
  head(): MetaInfo {
    return {
      title: 'Kronicle - All Teams',
    }
  },
  computed: {
    rows() {
      const that = this
      const rows = [] as Array<Array<Item>>
      const teamCount = this.teams.length
      let startIndex = 0
      while (startIndex < teamCount - 1) {
        const endIndex = Math.min(startIndex + 3, teamCount)
        const row = that.teams.slice(startIndex, endIndex).map(
          (team) =>
            ({
              team,
              components: getComponents(team),
            } as Item)
        )
        rows.push(row)
        startIndex = endIndex
      }
      return rows

      function getComponents(team: Team) {
        return that.components.filter((component) => {
          return component.teams.some((componentTeam) => {
            return (
              componentTeam.type !== 'previous' &&
              componentTeam.teamId === team.id
            )
          })
        })
      }
    },
  },
})
</script>
