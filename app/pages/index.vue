<template>
  <div class="m-3">
    <Intro id="intro" :title="introTitle" :markdown="introMarkdown" />

    <b-card-group columns>
      <b-card
        header="Kronicle"
        header-bg-variant="info"
        header-class="lead"
        no-body
      >
        <b-list-group flush>
          <b-list-group-item
            class="d-flex justify-content-between align-items-center text-muted"
          >
            Kronicle contains the following:
          </b-list-group-item>

          <b-list-group-item
            :to="{ name: 'all-areas' }"
            class="d-flex justify-content-between align-items-center"
          >
            <b>Areas</b>
            <span class="lead">
              <b-badge class="area-count" variant="primary" pill>{{
                  areaCount
                }}</b-badge>
            </span>
          </b-list-group-item>

          <b-list-group-item
            :to="{ name: 'all-teams' }"
            class="d-flex justify-content-between align-items-center"
          >
            <b>Teams</b>
            <span class="lead">
              <b-badge class="team-count" variant="primary" pill>{{
                  teamCount
                }}</b-badge>
            </span>
          </b-list-group-item>

          <b-list-group-item
            :to="{ name: 'all-components' }"
            class="d-flex justify-content-between align-items-center"
          >
            <b>Components</b>
            <span class="lead">
              <b-badge class="component-count" variant="primary" pill>{{
                  componentCount
                }}</b-badge>
            </span>
          </b-list-group-item>

          <b-list-group-item
            :to="{ name: 'all-components-tech-debts' }"
            class="d-flex justify-content-between align-items-center"
          >
            <b>Tech Debts</b>
            <span class="lead">
              <b-badge class="tech-debt-count" variant="primary" pill>{{
                  techDebtCount
                }}</b-badge>
            </span>
          </b-list-group-item>

          <b-list-group-item
            :to="{ name: 'all-components-tests' }"
            class="d-flex justify-content-between align-items-center"
          >
            <b>Test Results</b>
            <span class="lead">
              <b-badge class="test-result-count" variant="primary" pill>{{
                  testResultCount
                }}</b-badge>
            </span>
          </b-list-group-item>
        </b-list-group>
      </b-card>

      <b-card
        header="Scanners"
        header-bg-variant="info"
        header-class="lead"
        no-body
      >
        <b-list-group flush>
          <b-list-group-item
            class="d-flex justify-content-between align-items-center text-muted"
          >
            Scanners dynamically add data to Kronicle at runtime:
          </b-list-group-item>

          <b-list-group-item v-for="scanner in scanners" :key="scanner.id">
            <b class="scanner-id text-info">{{ scanner.id }}</b>
            <br />
            <Markdown :markdown="scanner.description" class="description" />
          </b-list-group-item>
        </b-list-group>
      </b-card>

      <b-card
        header="Tests"
        header-bg-variant="info"
        header-class="lead"
        no-body
      >
        <b-list-group flush>
          <b-list-group-item
            class="d-flex justify-content-between align-items-center text-muted"
          >
            Tests evaluate the data associated with components in Kronicle and
            return outcomes of pass, fail and not applicable
          </b-list-group-item>

          <b-list-group-item
            v-for="test in tests"
            :key="test.id"
            :to="{
              name: 'tests-testId',
              params: { testId: test.id },
            }"
          >
            <b class="test-id text-info">{{ test.id }}</b>
            <br />
            <Markdown :markdown="test.description" class="description" />
          </b-list-group-item>
        </b-list-group>
      </b-card>

      <b-card
        header="Platforms"
        header-bg-variant="info"
        header-class="lead"
        no-body
      >
        <b-list-group-item
          class="d-flex justify-content-between align-items-center text-muted"
        >
          The following platforms are associated with components in Kronicle:
        </b-list-group-item>

        <b-list-group flush>
          <b-list-group-item
            v-for="platformCount in platformCounts"
            :key="platformCount.item"
            :to="{
              name: 'all-components',
              query: { platformId: platformCount.item || 'undefined' },
            }"
            class="d-flex justify-content-between align-items-center"
          >
            <b class="platform">{{ platformCount.item || 'missing' }}</b>
            <span class="lead">
              <b-badge variant="primary" pill>
                {{ platformCount.count }}
              </b-badge>
            </span>
          </b-list-group-item>
        </b-list-group>
      </b-card>

      <b-card
        header="Tags"
        header-bg-variant="info"
        header-class="lead"
        no-body
      >
        <b-list-group flush>
          <b-list-group-item
            class="d-flex justify-content-between align-items-center text-muted"
          >
            The components in Kronicle are tagged with the following:
          </b-list-group-item>

          <b-list-group-item
            v-for="tagCount in tagCounts"
            :key="tagCount.item"
            :to="{
              name: 'all-components',
              query: { tag: tagCount.item },
            }"
            class="d-flex justify-content-between align-items-center"
          >
            <b class="tag">{{ tagCount.item }}</b>
            <span class="lead">
              <b-badge variant="primary" pill>
                {{ tagCount.count }}
              </b-badge>
            </span>
          </b-list-group-item>
        </b-list-group>
      </b-card>

      <b-card
        header="Component Types"
        header-bg-variant="info"
        header-class="lead"
        no-body
      >
        <b-list-group flush>
          <b-list-group-item
            class="d-flex justify-content-between align-items-center text-muted"
          >
            There are the following types of components in Kronicle:
          </b-list-group-item>

          <b-list-group-item
            v-for="componentTypeCount in componentTypeCounts"
            :key="componentTypeCount.item"
            :to="{
              name: 'all-components',
              query: { componentType: componentTypeCount.item },
            }"
            class="d-flex justify-content-between align-items-center"
          >
            <b class="component-type">{{ componentTypeCount.item }}</b>
            <span class="lead">
              <b-badge variant="primary" pill>
                {{ componentTypeCount.count }}
              </b-badge>
            </span>
          </b-list-group-item>
        </b-list-group>
      </b-card>
    </b-card-group>
  </div>
</template>

<style scoped>
.description {
  display: inline-block;
  color: white;
  text-decoration: none;
}

.description >>> p {
  margin-bottom: 0;
}
</style>

<script lang="ts">
import Vue from 'vue'
import {
  BBadge,
  BCard,
  BCardGroup,
  BListGroup,
  BListGroupItem,
} from 'bootstrap-vue'
import { MetaInfo } from 'vue-meta'
import { Area, Component, Scanner, Team, Test } from '~/types/kronicle-service'
import { ItemCount, itemCounts } from '~/src/arrayUtils'
import Intro from '~/components/Intro.vue'
import Markdown from '~/components/Markdown.vue'

export default Vue.extend({
  components: {
    'b-badge': BBadge,
    'b-card': BCard,
    'b-card-group': BCardGroup,
    'b-list-group': BListGroup,
    'b-list-group-item': BListGroupItem,
    Intro,
    Markdown,
  },
  async asyncData({ $config }) {
    const areas = await fetch(
      `${$config.serviceBaseUrl}/v1/areas?fields=areas(id)`
    )
      .then((res) => res.json())
      .then((json) => json.areas as Area[])

    const teams = await fetch(
      `${$config.serviceBaseUrl}/v1/teams?fields=teams(id)`
    )
      .then((res) => res.json())
      .then((json) => json.teams as Team[])

    const components = await fetch(
      `${$config.serviceBaseUrl}/v1/components?fields=components(id,typeId,platformId,tags,techDebts(doesNotExist),testResults(doesNotExist))`
    )
      .then((res) => res.json())
      .then((json) => json.components as Component[])

    const scanners = await fetch(
      `${$config.serviceBaseUrl}/v1/scanners?fields=scanners(id,description)`
    )
      .then((res) => res.json())
      .then((json) => json.scanners as Scanner[])

    const tests = await fetch(
      `${$config.serviceBaseUrl}/v1/tests?fields=tests(id,description)`
    )
      .then((res) => res.json())
      .then((json) => json.tests as Test[])

    return {
      areas,
      teams,
      components,
      scanners,
      tests,
    }
  },
  data() {
    return {
      introTitle: this.$config.introTitle as String | undefined,
      introMarkdown: this.$config.introMarkdown as String | undefined,
      areas: [] as Area[],
      teams: [] as Team[],
      components: [] as Component[],
      scanners: [] as Scanner[],
      tests: [] as Test[],
    }
  },
  head(): MetaInfo {
    return {
      title: 'Kronicle',
    }
  },
  computed: {
    areaCount(): number {
      return this.areas.length
    },
    teamCount(): number {
      return this.teams.length
    },
    componentCount(): number {
      return this.components.length
    },
    componentTypeCounts(): ItemCount<string>[] {
      return itemCounts(this.components.map((component) => component.typeId))
    },
    platformCounts(): ItemCount<string>[] {
      return itemCounts(
        this.components.map((component) => component.platformId)
      )
    },
    tagCounts(): ItemCount<string>[] {
      return itemCounts(
        this.components.flatMap((component) => component.tags ?? [])
      )
    },
    techDebtCount(): number {
      return this.components.flatMap((component) => component.techDebts ?? [])
        .length
    },
    testResultCount(): number {
      return this.components.flatMap((component) => component.testResults ?? [])
        .length
    },
  },
})
</script>
