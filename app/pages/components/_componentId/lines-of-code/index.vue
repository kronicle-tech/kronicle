<template>
  <div class="m-3">
    <h1 class="text-info my-3">{{ component.name }} - Lines of Code</h1>

    <ComponentTabs
      :component-id="component.id"
      :component-available-data="componentAvailableData"
    />

    <b-card title="Total Lines of Code" class="my-3">
      <b-list-group>
        <b-list-group-item :variant="linesOfCodeCountVariant">
          <span class="display-1">
            <FormattedNumber :value="linesOfCodeCount" />
          </span>
          lines of code{{ linesOfCodeCount === 1 ? '' : 's' }}
        </b-list-group-item>
      </b-list-group>
    </b-card>

    <b-card title="Lines of Code by File Extension">
      <b-list-group>
        <b-list-group-item
          v-for="fileExtensionCount in fileExtensionCounts"
          :key="fileExtensionCount.fileExtension"
          class="d-flex justify-content-between align-items-center"
        >
          {{ fileExtensionCount.fileExtension }}
          <b-badge variant="primary" pill>
            <FormattedNumber :value="fileExtensionCount.count" />
          </b-badge>
        </b-list-group-item>
      </b-list-group>
    </b-card>
  </div>
</template>

<script lang="ts">
import Vue from 'vue'
import { MetaInfo } from 'vue-meta'
import { BBadge, BCard, BListGroup, BListGroupItem } from 'bootstrap-vue'
import {
  Component,
  FileExtensionCount,
  LinesOfCodeState,
} from '~/types/kronicle-service'
import ComponentTabs from '~/components/ComponentTabs.vue'
import FormattedNumber from '~/components/FormattedNumber.vue'
import { fetchComponentAvailableData } from '~/src/fetchComponentAvailableData'

export default Vue.extend({
  components: {
    'b-badge': BBadge,
    'b-card': BCard,
    'b-list-group': BListGroup,
    'b-list-group-item': BListGroupItem,
    ComponentTabs,
    FormattedNumber,
  },
  async asyncData({ $config, route }) {
    const componentAvailableData = await fetchComponentAvailableData(
      $config,
      route
    )

    const component = await fetch(
      `${$config.serviceBaseUrl}/v1/components/${route.params.componentId}?stateType=lines-of-code&fields=component(id,name,states)`
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
      title: `Kronicle - ${this.component.name} - Lines of Code`,
    }
  },
  computed: {
    linesOfCode(): LinesOfCodeState | undefined {
      return this.component.states.find(
        (state) => state.type === 'lines-of-code'
      ) as LinesOfCodeState | undefined
    },
    linesOfCodeCount(): number {
      return this.linesOfCode?.count ?? 0
    },
    linesOfCodeCountVariant(): string {
      return this.linesOfCodeCount > 100000 ? 'danger' : 'success'
    },
    fileExtensionCounts(): FileExtensionCount[] {
      if (!this.linesOfCode) {
        return []
      }

      return this.linesOfCode.fileExtensionCounts.map(
        ({ fileExtension, count }) => {
          return {
            fileExtension: fileExtension || 'No file extension',
            count,
          } as FileExtensionCount
        }
      )
    },
  },
})
</script>
