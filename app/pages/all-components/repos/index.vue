<template>
  <div>
    <AllComponentsTabs />
    <div class="main">
      <table
        class="table table-dark table-bordered table-striped mt-2"
        style="width: 100%"
      >
        <thead>
          <tr>
            <th class="component-type">Type</th>
            <th class="component-name">Name</th>
            <th>Repo</th>
            <th>Repo Age</th>
            <th>Time Since Last Commit</th>
            <th>Commit Count</th>
            <th>Author Count</th>
            <th>Committer Count</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="component in filteredComponents" :key="component.id">
            <td>{{ component.typeId }}</td>
            <td class="component-name table-primary">
              <ComponentName :component="component" />
            </td>
            <td>
              <Repo :repo="component.repo" />
            </td>
            <td>
              <FormattedAge
                :value="
                  component.gitRepo
                    ? component.gitRepo.firstCommitTimestamp
                    : null
                "
              />
            </td>
            <td>
              <FormattedAge
                :value="
                  component.gitRepo
                    ? component.gitRepo.lastCommitTimestamp
                    : null
                "
              />
            </td>
            <td>
              <FormattedNumber
                :value="
                  component.gitRepo ? component.gitRepo.commitCount : null
                "
              />
            </td>
            <td>
              <FormattedNumber
                :value="
                  component.gitRepo ? component.gitRepo.authorCount : null
                "
              />
            </td>
            <td>
              <FormattedNumber
                :value="
                  component.gitRepo ? component.gitRepo.committerCount : null
                "
              />
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <div class="panel">
      <ComponentFilters :components="components" />
    </div>
  </div>
</template>

<style scoped>
.main {
  float: left;
  width: calc(100% - 275px);
}

.panel {
  float: right;
  width: 250px;
}
</style>

<script lang="ts">
import Vue from 'vue'
import { MetaInfo } from 'vue-meta'
import { Component } from '~/types/kronicle-service'
import AllComponentsTabs from '~/components/AllComponentsTabs.vue'
import ComponentFilters from '~/components/ComponentFilters.vue'
import ComponentName from '~/components/ComponentName.vue'
import FormattedAge from '~/components/FormattedAge.vue'
import FormattedNumber from '~/components/FormattedNumber.vue'
import Repo from '~/components/Repo.vue'

export default Vue.extend({
  components: {
    AllComponentsTabs,
    ComponentFilters,
    ComponentName,
    FormattedAge,
    FormattedNumber,
    Repo,
  },
  async asyncData({ $config, route, store }) {
    const components = await fetch(
      `${$config.serviceBaseUrl}/v1/components?fields=components(id,name,typeId,tags,teams,platformId,repo,gitRepo)`
    )
      .then((res) => res.json())
      .then((json) => json.components)

    store.commit('componentFilters/initialize', {
      components,
      route,
    })

    return {
      components,
    }
  },
  data() {
    return {
      components: [] as Component[],
    }
  },
  computed: {
    filteredComponents(): Component[] {
      return this.$store.state.componentFilters.filteredComponents
    },
  },
  head(): MetaInfo {
    return {
      title: 'Kronicle - All Components - Repos',
    }
  },
})
</script>
