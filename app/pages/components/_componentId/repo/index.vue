<template>
  <div>
    <h1 class="text-info my-3">{{ component.name }} - Errors</h1>
    <ComponentTabs :component-id="component.id" />

    <div class="row">
      <div class="col-sm-6">
        <b-card title="Repo">
          <Repo :repo="component.repo" />
        </b-card>

        <b-list-group v-if="component.gitRepo" class="my-3">
          <b-list-group-item
            class="d-flex justify-content-between align-items-center"
          >
            Repo Age
            <b-badge variant="primary" pill>
              <FormattedAge
                :value="
                  component.gitRepo
                    ? component.gitRepo.firstCommitTimestamp
                    : null
                "
              />
            </b-badge>
          </b-list-group-item>

          <b-list-group-item
            class="d-flex justify-content-between align-items-center"
          >
            Time Since Last Commit
            <b-badge variant="primary" pill>
              <FormattedAge
                :value="
                  component.gitRepo
                    ? component.gitRepo.lastCommitTimestamp
                    : null
                "
              />
            </b-badge>
          </b-list-group-item>

          <b-list-group-item
            class="d-flex justify-content-between align-items-center"
          >
            Commit Count
            <b-badge variant="primary" pill>
              <FormattedNumber
                :value="
                  component.gitRepo ? component.gitRepo.commitCount : null
                "
              />
            </b-badge>
          </b-list-group-item>

          <b-list-group-item
            class="d-flex justify-content-between align-items-center"
          >
            Author Count
            <b-badge variant="primary" pill>
              <FormattedNumber
                :value="
                  component.gitRepo ? component.gitRepo.authorCount : null
                "
              />
            </b-badge>
          </b-list-group-item>

          <b-list-group-item
            class="d-flex justify-content-between align-items-center"
          >
            Committer Count
            <b-badge variant="primary" pill>
              <FormattedNumber
                :value="
                  component.gitRepo ? component.gitRepo.committerCount : null
                "
              />
            </b-badge>
          </b-list-group-item>
        </b-list-group>
      </div>

      <div v-if="component.gitRepo" class="col-sm-6">
        <b-card title="Authors">
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
      </div>

      <b-card v-if="!component.gitRepo" title="No Git information">
        <b-card-text>
          Not git-based information is available for this component
        </b-card-text>
      </b-card>
    </div>
  </div>
</template>

<script lang="ts">
import Vue from 'vue'
import { MetaInfo } from 'vue-meta'
import {
  BBadge,
  BCard,
  BCardText,
  BListGroup,
  BListGroupItem,
} from 'bootstrap-vue'
import { Component, Identity } from '~/types/component-catalog-service'
import ComponentTabs from '~/components/ComponentTabs.vue'
import EmailAddress from '~/components/EmailAddress.vue'
import FormattedAge from '~/components/FormattedAge.vue'
import FormattedNumber from '~/components/FormattedNumber.vue'
import FormattedDate from '~/components/FormattedDate.vue'
import Repo from '~/components/Repo.vue'

export default Vue.extend({
  components: {
    'b-badge': BBadge,
    'b-card': BCard,
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
    const component = await fetch(
      `${$config.serviceBaseUrl}/v1/components/${route.params.componentId}?fields=component(id,name,repo,gitRepo)`
    )
      .then((res) => res.json())
      .then((json) => json.component as Component)

    return {
      component,
    }
  },
  data() {
    return {
      component: {} as Component,
    }
  },
  computed: {
    authors(): Identity[] {
      return this.component.gitRepo.authors.slice().sort((a, b) => {
        return -a.lastCommitTimestamp.localeCompare(b.lastCommitTimestamp)
      })
    },
  },
  head(): MetaInfo {
    return {
      title: `Component Catalog - ${this.component.name} - Repo`,
    }
  },
})
</script>
