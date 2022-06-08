<template>
  <div class="m-3">
    <h1 class="text-info my-3">{{ component.name }} - Repo</h1>

    <ComponentTabs
      :component-id="component.id"
      :component-available-data="componentAvailableData"
    />

    <b-card-group deck class="my-3">
      <b-card title="Repo">
        <Repo :repo="component.repo" />
      </b-card>

      <b-card v-if="gitRepo" title="Stats">
        <b-list-group>
          <b-list-group-item
            variant="secondary"
            class="d-flex justify-content-between align-items-center"
          >
            Repo Age
            <b-badge variant="primary" pill>
              <FormattedAge
                :value="gitRepo ? gitRepo.firstCommitTimestamp : null"
              />
            </b-badge>
          </b-list-group-item>

          <b-list-group-item
            variant="secondary"
            class="d-flex justify-content-between align-items-center"
          >
            Time Since Last Commit
            <b-badge variant="primary" pill>
              <FormattedAge
                :value="gitRepo ? gitRepo.lastCommitTimestamp : null"
              />
            </b-badge>
          </b-list-group-item>

          <b-list-group-item
            variant="secondary"
            class="d-flex justify-content-between align-items-center"
          >
            Commit Count
            <b-badge variant="primary" pill>
              <FormattedNumber :value="gitRepo ? gitRepo.commitCount : null" />
            </b-badge>
          </b-list-group-item>

          <b-list-group-item
            variant="secondary"
            class="d-flex justify-content-between align-items-center"
          >
            Author Count
            <b-badge variant="primary" pill>
              <FormattedNumber :value="gitRepo ? gitRepo.authorCount : null" />
            </b-badge>
          </b-list-group-item>

          <b-list-group-item
            variant="secondary"
            class="d-flex justify-content-between align-items-center"
          >
            Committer Count
            <b-badge variant="primary" pill>
              <FormattedNumber
                :value="gitRepo ? gitRepo.committerCount : null"
              />
            </b-badge>
          </b-list-group-item>
        </b-list-group>
      </b-card>
    </b-card-group>

    <b-card-group deck>
      <b-card v-if="gitRepo" title="Authors">
        <table class="table table-dark">
          <thead>
            <tr>
              <th>Names</th>
              <th>Email Address</th>
              <th>Commits</th>
              <th>First Commit</th>
              <th>Last Commit</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="(author, authorIndex) in authors" :key="authorIndex">
              <td>
                <span
                  v-for="(authorName, authorNameIndex) in author.names"
                  :key="authorNameIndex"
                >
                  {{ authorName }}
                  <br />
                </span>
              </td>
              <td>
                <EmailAddress :email-address="author.emailAddress" />
              </td>
              <td>
                <FormattedNumber :value="author.commitCount" />
              </td>
              <td>
                <FormattedDate :value="author.firstCommitTimestamp" />
              </td>
              <td>
                <FormattedDate :value="author.lastCommitTimestamp" />
              </td>
            </tr>
          </tbody>
        </table>
      </b-card>

      <b-card v-if="!gitRepo" title="No Git information">
        <b-card-text>
          Not git-based information is available for this component
        </b-card-text>
      </b-card>
    </b-card-group>
  </div>
</template>

<script lang="ts">
import Vue from 'vue'
import { MetaInfo } from 'vue-meta'
import {
  BBadge,
  BCard,
  BCardGroup,
  BCardText,
  BListGroup,
  BListGroupItem,
} from 'bootstrap-vue'
import { Component, GitRepoState, Identity } from '~/types/kronicle-service'
import ComponentTabs from '~/components/ComponentTabs.vue'
import EmailAddress from '~/components/EmailAddress.vue'
import FormattedAge from '~/components/FormattedAge.vue'
import FormattedNumber from '~/components/FormattedNumber.vue'
import FormattedDate from '~/components/FormattedDate.vue'
import Repo from '~/components/Repo.vue'
import { fetchComponentAvailableData } from '~/src/fetchComponentAvailableData'
import { findComponentState } from '~/src/componentStateUtils'

export default Vue.extend({
  components: {
    'b-badge': BBadge,
    'b-card': BCard,
    'b-card-group': BCardGroup,
    'b-card-text': BCardText,
    'b-list-group': BListGroup,
    'b-list-group-item': BListGroupItem,
    ComponentTabs,
    EmailAddress,
    FormattedAge,
    FormattedNumber,
    FormattedDate,
    Repo,
  },
  async asyncData({ $config, route }) {
    const componentAvailableData = await fetchComponentAvailableData(
      $config,
      route
    )

    const component = await fetch(
      `${$config.serviceBaseUrl}/v1/components/${route.params.componentId}?stateType=git-repo&fields=component(id,name,teams,states)`
    )
      .then((res) => res.json())
      .then((json) => json.component as Component)

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
      title: `Kronicle - ${this.component.name} - Repo`,
    }
  },
  computed: {
    gitRepo(): GitRepoState | undefined {
      return findComponentState(this.component, 'git-repo')
    },
    authors(): Identity[] {
      if (!this.gitRepo) {
        return []
      }
      return this.gitRepo.authors.slice().sort((a, b) => {
        return -a.lastCommitTimestamp.localeCompare(b.lastCommitTimestamp)
      })
    },
  },
})
</script>
