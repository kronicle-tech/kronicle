<template>
  <div v-if="components && components.length > 0">
    <b-form-group
      v-if="
        testOutcomesFilterEnabled &&
        testOutcomeOptions &&
        testOutcomeOptions.length > 0
      "
      label="Test Outcomes"
    >
      <b-form-checkbox-group
        v-model="testOutcomes"
        :options="testOutcomeOptions"
        name="testOutcome"
        stacked
      ></b-form-checkbox-group>
    </b-form-group>

    <b-form-group
      v-if="teamIdOptions && teamIdOptions.length > 0"
      label="Teams"
    >
      <b-form-checkbox-group
        v-model="teamIds"
        :options="teamIdOptions"
        name="team"
        stacked
      ></b-form-checkbox-group>
    </b-form-group>

    <b-form-group v-if="tagOptions && tagOptions.length > 0" label="Tags">
      <b-form-checkbox-group
        v-model="tags"
        :options="tagOptions"
        name="tag"
        stacked
      ></b-form-checkbox-group>
    </b-form-group>

    <b-form-group
      v-if="componentTypeIdOptions && componentTypeIdOptions.length > 0"
      label="Component Types"
    >
      <b-form-checkbox-group
        v-model="componentTypeIds"
        :options="componentTypeIdOptions"
        name="componentTypeId"
        stacked
      ></b-form-checkbox-group>
    </b-form-group>

    <b-form-group
      v-if="platformIdOptions && platformIdOptions.length > 0"
      label="Platforms"
    >
      <b-form-checkbox-group
        v-model="platformIds"
        :options="platformIdOptions"
        name="platformId"
        stacked
      ></b-form-checkbox-group>
    </b-form-group>

    <b-form-group
      v-if="
        componentFilterEnabled &&
        componentIdOptions &&
        componentIdOptions.length > 0
      "
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
  </div>
</template>

<script lang="ts">
import { Component as VueComponent, Prop, Vue } from 'vue-property-decorator'
import { BFormCheckboxGroup, BFormGroup, BFormSelect } from 'bootstrap-vue'
import { Component } from '~/types/kronicle-service'
import { distinctArrayElements } from '~/src/arrayUtils'

interface Option {
  value: string | null
  text: string
}

@VueComponent({
  components: {
    'b-form-checkbox-group': BFormCheckboxGroup,
    'b-form-group': BFormGroup,
    'b-form-select': BFormSelect,
  },
})
export default class ComponentFilters extends Vue {
  @Prop({ default: () => [] }) readonly components!: Component[]
  @Prop({ default: false }) readonly testOutcomesFilterEnabled!: boolean
  @Prop({ default: true }) readonly componentFilterEnabled!: boolean

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
