<template>
  <div v-if="components && components.length > 0"
       class="mt-3 mb-4">

    <b-button
      v-if="toggleEnabled"
      id="toggleFilters"
      v-b-toggle.filters
      variant="primary"
    >
      <b-icon icon="filter" aria-hidden="true" /> Filters
    </b-button>

    <b-collapse
      id="filters"
      :visible.sync="collapseVisible"
      class="mt-3"
    >
      <b-card-group
        columns
        :style="cardGroupStyle"
      >
        <slot></slot>

        <b-card
          v-if="
                environmentIdFilterEnabled &&
                environmentIdOptions &&
                environmentIdOptions.length > 0
              "
          bg-variant="secondary"
        >
          <b-form-group
            label="Environments"
          >
            <b-form-checkbox-group
              v-model="environmentIds"
              :options="environmentIdOptions"
              name="environmentId"
              stacked
            ></b-form-checkbox-group>
          </b-form-group>
        </b-card>

        <b-card
          v-if="
                pluginIdFilterEnabled &&
                pluginIdOptions &&
                pluginIdOptions.length > 0
              "
          bg-variant="secondary"
        >
          <b-form-group
            label="Plugins"
          >
            <b-form-checkbox-group
              v-model="pluginIds"
              :options="pluginIdOptions"
              name="pluginId"
              stacked
            ></b-form-checkbox-group>
          </b-form-group>
        </b-card>

        <b-card
          v-if="
                testOutcomesFilterEnabled &&
                testOutcomeOptions &&
                testOutcomeOptions.length > 0
              "
          bg-variant="secondary"
        >
          <b-form-group
            label="Test Outcomes"
          >
            <b-form-checkbox-group
              v-model="testOutcomes"
              :options="testOutcomeOptions"
              name="testOutcome"
              stacked
            ></b-form-checkbox-group>
          </b-form-group>
        </b-card>

        <b-card
          v-if="teamIdOptions && teamIdOptions.length > 0"
          bg-variant="secondary"
        >
          <b-form-group
            label="Teams"
          >
            <b-form-checkbox-group
              v-model="teamIds"
              :options="teamIdOptions"
              name="team"
              stacked
            ></b-form-checkbox-group>
          </b-form-group>
        </b-card>

        <b-card
          v-if="tagOptions && tagOptions.length > 0"
          bg-variant="secondary"
        >
          <b-form-group label="Tags">
            <b-form-checkbox-group
              v-model="tags"
              :options="tagOptions"
              name="tag"
              stacked
            ></b-form-checkbox-group>
          </b-form-group>
        </b-card>

        <b-card
          v-if="componentTypeIdOptions && componentTypeIdOptions.length > 0"
          bg-variant="secondary"
        >
          <b-form-group
            label="Component Types"
          >
            <b-form-checkbox-group
              v-model="componentTypeIds"
              :options="componentTypeIdOptions"
              name="componentTypeId"
              stacked
            ></b-form-checkbox-group>
          </b-form-group>
        </b-card>

        <b-card
          v-if="platformIdOptions && platformIdOptions.length > 0"
          bg-variant="secondary"
        >
          <b-form-group
            label="Platforms"
          >
            <b-form-checkbox-group
              v-model="platformIds"
              :options="platformIdOptions"
              name="platformId"
              stacked
            ></b-form-checkbox-group>
          </b-form-group>
        </b-card>

        <b-card
          v-if="
            componentFilterEnabled &&
            componentIdOptions &&
            componentIdOptions.length > 0
          "
          bg-variant="secondary"
        >
          <b-form-group
            label="Component"
            label-for="component-filter"
          >
            <b-form-select
              id="component-filter"
              v-model="componentId"
              :options="componentIdOptions"
              size="sm"
            />
          </b-form-group>
        </b-card>
      </b-card-group>
    </b-collapse>
  </div>
</template>

<script lang="ts">
import { Component as VueComponent, Prop, Vue } from 'vue-property-decorator'
import {
  BButton,
  BCard,
  BCardGroup,
  BCollapse,
  BFormCheckboxGroup,
  BFormGroup,
  BFormSelect, BIcon,
  VBToggle
} from 'bootstrap-vue'
import { Component } from '~/types/kronicle-service'
import { distinctArrayElements } from '~/src/arrayUtils'

interface Option {
  value: string | null
  text: string
}

@VueComponent({
  components: {
    'b-button': BButton,
    'b-card': BCard,
    'b-card-group': BCardGroup,
    'b-collapse': BCollapse,
    'b-form-checkbox-group': BFormCheckboxGroup,
    'b-form-group': BFormGroup,
    'b-form-select': BFormSelect,
    'b-icon': BIcon,
  },
  directives: {
    'b-toggle': VBToggle,
  }
})
export default class ComponentFilters extends Vue {
  @Prop({ default: false }) readonly environmentIdFilterEnabled!: boolean
  @Prop({ default: false }) readonly pluginIdFilterEnabled!: boolean
  @Prop({ default: false }) readonly testOutcomesFilterEnabled!: boolean
  @Prop({ default: true }) readonly componentFilterEnabled!: boolean
  @Prop({ default: true }) readonly toggleEnabled!: boolean
  @Prop({ default: undefined }) readonly columnCount!: number | undefined

  get components(): Component[] {
    return this.$store.state.componentFilters.components ?? []
  }

  get cardGroupStyle(): string {
    if (this.columnCount === undefined) {
      return ''
    }
    return `-webkit-column-count: ${this.columnCount}; -moz-column-count: ${this.columnCount}; column-count: ${this.columnCount}`
  }

  get allEnvironmentIds(): string[] {
    return distinctArrayElements(
      this.components
        .flatMap((component) => component.state?.environments ?? [])
        .map((environment) => environment.id)
    )
  }

  get environmentIdOptions(): Option[] {
    return this.allEnvironmentIds.map((environmentId) => {
      return {
        value: environmentId,
        text: environmentId,
      }
    })
  }

  get environmentIds(): string[] {
    return this.$store.state.componentFilters.environmentIds
  }

  set environmentIds(value: string[]) {
    this.$store.commit('componentFilters/setEnvironmentIds', value)
  }

  get allPluginIds(): string[] {
    return distinctArrayElements(
      this.components
        .flatMap((component) =>
          (component.state?.environments ?? []).flatMap(environment =>
            (environment.plugins ?? []).map((plugin) => plugin.id)
          )
        )
    )
  }

  get pluginIdOptions(): Option[] {
    return this.allPluginIds.map((pluginId) => {
      return {
        value: pluginId,
        text: pluginId,
      }
    })
  }

  get pluginIds(): string[] {
    return this.$store.state.componentFilters.pluginIds
  }

  set pluginIds(value: string[]) {
    this.$store.commit('componentFilters/setPluginIds', value)
  }

  get allTestOutcomes(): string[] {
    return distinctArrayElements(
      this.components
        .flatMap((component) => component.testResults ?? [])
        .map((testResult) => testResult.outcome)
    )
  }

  get testOutcomeOptions(): Option[] {
    return this.allTestOutcomes.map((testOutcome) => {
      return {
        value: testOutcome,
        text: testOutcome,
      }
    })
  }

  get testOutcomes(): string[] {
    return this.$store.state.componentFilters.testOutcomes
  }

  set testOutcomes(value: string[]) {
    this.$store.commit('componentFilters/setTestOutcomes', value)
  }

  get allTeamIds(): string[] {
    return distinctArrayElements(
      this.components
        .flatMap((component) => component.teams ?? [])
        .map((componentTeam) => componentTeam.teamId)
    )
  }

  get teamIdOptions(): Option[] {
    return this.allTeamIds.map((teamId) => {
      return {
        value: teamId,
        text: teamId,
      }
    })
  }

  get teamIds(): string[] {
    return this.$store.state.componentFilters.teamIds
  }

  set teamIds(value: string[]) {
    this.$store.commit('componentFilters/setTeamIds', value)
  }

  get allTags(): string[] {
    return distinctArrayElements(
      this.components.flatMap((component) => component.tags ?? [])
    )
  }

  get tagOptions(): Option[] {
    return this.allTags.map((tag) => {
      return {
        value: tag,
        text: tag,
      }
    })
  }

  get tags(): string[] {
    return this.$store.state.componentFilters.tags
  }

  set tags(value) {
    this.$store.commit('componentFilters/setTags', value)
  }

  get allComponentTypeIds(): string[] {
    return distinctArrayElements(
      this.components.flatMap((component) => component.typeId)
    )
  }

  get componentTypeIdOptions(): Option[] {
    return this.allComponentTypeIds.map((componentTypeId) => {
      return {
        value: componentTypeId,
        text: componentTypeId,
      }
    })
  }

  get componentTypeIds(): string[] {
    return this.$store.state.componentFilters.componentTypeIds
  }

  set componentTypeIds(value) {
    this.$store.commit('componentFilters/setComponentTypeIds', value)
  }

  get allPlatformIds(): string[] {
    return distinctArrayElements(
      this.components.map((component) => component.platformId)
    ).map((platformId) => platformId || 'undefined')
  }

  get platformIdOptions(): Option[] {
    return this.allPlatformIds.map((platformId) => {
      return {
        value: platformId,
        text: platformId,
      }
    })
  }

  get platformIds(): string[] {
    return this.$store.state.componentFilters.platformIds
  }

  set platformIds(value) {
    this.$store.commit('componentFilters/setPlatformIds', value)
  }

  get allComponentIds(): string[] {
    return this.components.map((component) => component.id).sort()
  }

  get componentIdOptions(): Option[] {
    const options = this.allComponentIds.map((componentId) => {
      return {
        value: componentId,
        text: componentId,
      } as Option
    })
    options.unshift({
      value: null,
      text: 'Please select a component',
    })
    return options
  }

  get componentId(): string | undefined {
    return this.$store.state.componentFilters.componentId ?? null
  }

  set componentId(value) {
    this.$store.commit('componentFilters/setComponentId', value)
  }
}
</script>
