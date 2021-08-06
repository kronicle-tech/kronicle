<template>
  <div>
    <div v-if="rows && rows.length > 0">
      <b-alert show="30" dismissible variant="info" class="my-3">
        The dependencies on this page come from Zipkin. The visualisation is not
        out-of-the-box Zipkin functionality and is bespoke to the Component
        Catalog.
      </b-alert>

      <table class="table table-dark">
        <thead>
          <tr>
            <th>Source Component</th>
            <th>Source Span</th>
            <th>Source Tags</th>
            <th>Target Component</th>
            <th>Target Span</th>
            <th>Target Tags</th>
            <th>Sample Size</th>
            <th>Sample Start</th>
            <th>Sample End</th>
            <th>Min</th>
            <th>p50</th>
            <th>p90</th>
            <th>p99</th>
            <th>p99.9</th>
            <th>Max</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="(row, rowIndex) in rows" :key="rowIndex">
            <td :class="row.isSource ? 'table-primary' : ''">
              <ComponentName
                v-if="row.sourceComponent"
                :component="row.sourceComponent"
              />
              <span v-else-if="row.sourceNode">
                {{ row.sourceNode.componentId }}
                <b-badge variant="warning">Unrecognised Component</b-badge>
              </span>
              <b-badge v-else variant="danger">Unknown Component</b-badge>
            </td>
            <td>
              <span v-if="row.sourceNode">{{ row.sourceNode.spanName }}</span>
            </td>
            <td>
              <ul v-if="row.sourceTags.length > 0">
                <li v-for="tag in row.sourceTags" :key="tag.key">
                  {{ tag.key }}: {{ tag.value }}
                </li>
              </ul>
            </td>
            <td :class="row.isTarget ? 'table-primary' : ''">
              <ComponentName
                v-if="row.targetComponent"
                :component="row.targetComponent"
              />
              <span v-else-if="row.targetNode">
                {{ row.targetNode.componentId }}
                <b-badge variant="warning">Unrecognised Component</b-badge>
              </span>
              <b-badge v-else variant="danger">Unknown Component</b-badge>
            </td>
            <td>
              {{ row.targetNode.spanName }}
            </td>
            <td>
              <ul v-if="row.targetTags.length > 0">
                <li v-for="tag in row.targetTags" :key="tag.key">
                  {{ tag.key }}: {{ tag.value }}
                </li>
              </ul>
            </td>
            <td><FormattedNumber :value="row.sampleSize" /> spans</td>
            <td><FormattedDateTime :value="row.startTimestamp" /></td>
            <td><FormattedDateTime :value="row.endTimestamp" /></td>
            <td>
              <span v-if="row.duration">
                <FormattedNumber :value="row.duration.min" />ms
              </span>
              <span v-else>Unknown</span>
            </td>
            <td>
              <span v-if="row.duration">
                <FormattedNumber :value="row.duration.p50" />ms
              </span>
              <span v-else>Unknown</span>
            </td>
            <td>
              <span v-if="row.duration">
                <FormattedNumber :value="row.duration.p90" />ms
              </span>
              <span v-else>Unknown</span>
            </td>
            <td>
              <span v-if="row.duration">
                <FormattedNumber :value="row.duration.p99" />ms
              </span>
              <span v-else>Unknown</span>
            </td>
            <td>
              <span v-if="row.duration">
                <FormattedNumber :value="row.duration.p99Point9" />ms
              </span>
              <span v-else>Unknown</span>
            </td>
            <td>
              <span v-if="row.duration">
                <FormattedNumber :value="row.duration.max" />ms
              </span>
              <span v-else>Unknown</span>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
    <div v-else>
      <b-alert show variant="info" class="my-3">
        No response times are available for this component. Possibly the
        component may not have been integrated with Zipkin yet.
      </b-alert>
    </div>
  </div>
</template>

<script lang="ts">
import Vue, { PropType } from 'vue'
import { BAlert, BBadge } from 'bootstrap-vue'
import {
  Component,
  SummaryComponentDependency,
  SummaryComponentDependencyDuration,
  SummaryComponentDependencyNode,
  SummarySubComponentDependencies,
  SummarySubComponentDependencyNode,
} from '~/types/component-catalog-service'
import ComponentName from '~/components/ComponentName.vue'
import FormattedNumber from '~/components/FormattedNumber.vue'
import FormattedDateTime from '~/components/FormattedDateTime.vue'

type Direction = 'upstream' | 'downstream'

interface Row {
  sourceNode: SummarySubComponentDependencyNode
  sourceTags: Tag[]
  sourceComponent: Component
  isSource: boolean
  targetNode: SummarySubComponentDependencyNode
  targetTags: Tag[]
  targetComponent: Component
  isTarget: boolean
  sampleSize: number
  startTimestamp: string
  endTimestamp: string
  duration: Duration
}

interface Tag {
  key: string
  value: string
}

interface Duration {
  min: number
  max: number
  p50: number
  p90: number
  p99: number
  p99Point9: number
}

export default Vue.extend({
  components: {
    'b-alert': BAlert,
    'b-badge': BBadge,
    ComponentName,
    FormattedDateTime,
    FormattedNumber,
  },
  props: {
    componentId: {
      type: String,
      required: true,
    },
    direction: {
      type: String as PropType<Direction>,
      required: true,
    },
    allComponents: {
      type: Array as PropType<Component[]>,
      required: true,
    },
    subComponentDependencies: {
      type: Object as PropType<SummarySubComponentDependencies>,
      default: undefined,
    },
  },
  computed: {
    rows(): Row[] {
      const that = this
      if (!that.subComponentDependencies) {
        return []
      }

      const componentNodeIndexes = that.subComponentDependencies.nodes
        .map((node, nodeIndex) =>
          node.componentId === that.componentId ? nodeIndex : undefined
        )
        .filter((nodeIndex): nodeIndex is number => nodeIndex !== undefined)

      if (componentNodeIndexes.length === 0) {
        return []
      }

      return that.subComponentDependencies.dependencies
        .filter(
          that.dependencyMatchesThisComponentAndDirection(
            that.direction,
            componentNodeIndexes
          )
        )
        .map((dependency) => {
          const sourceNode = that.getNodeByIndex(dependency.sourceIndex)
          const targetNode = that.getNodeByIndex(dependency.targetIndex)
          return {
            sourceNode,
            sourceTags: that.getTags(sourceNode),
            sourceComponent: that.getComponentById(sourceNode?.componentId),
            isSource:
              sourceNode !== undefined &&
              sourceNode.componentId === that.componentId,
            targetNode,
            targetTags: that.getTags(targetNode),
            targetComponent: that.getComponentById(targetNode?.componentId),
            isTarget:
              targetNode !== undefined &&
              targetNode.componentId === that.componentId,
            sampleSize: dependency.sampleSize,
            startTimestamp: dependency.startTimestamp,
            endTimestamp: dependency.endTimestamp,
            duration: that.mapDuration(dependency.duration),
          } as Row
        })
        .sort((a, b) => (b.duration?.max ?? 0) - (a.duration?.max ?? 0))
    },
  },
  methods: {
    dependencyMatchesThisComponentAndDirection(
      direction: Direction,
      componentNodeIndexes: number[]
    ) {
      return (dependency: SummaryComponentDependency) => {
        return (
          (direction === 'downstream' &&
            dependency.sourceIndex !== undefined &&
            componentNodeIndexes.includes(dependency.sourceIndex)) ||
          (direction === 'upstream' &&
            (dependency.sourceIndex === undefined ||
              !componentNodeIndexes.includes(dependency.sourceIndex)) &&
            componentNodeIndexes.includes(dependency.targetIndex))
        )
      }
    },
    getNodeByIndex(
      index?: number
    ): SummarySubComponentDependencyNode | undefined {
      return index !== undefined
        ? this.subComponentDependencies.nodes[index]
        : undefined
    },
    getComponentById(id?: string): Component | undefined {
      return id !== undefined
        ? this.allComponents.find((component) => component.id === id)
        : undefined
    },
    getTags(
      node?: SummaryComponentDependencyNode | SummarySubComponentDependencyNode
    ): Tag[] {
      if (node === undefined || !('tags' in node)) {
        return []
      }

      return Object.keys(node.tags)
        .map(
          (key) =>
            ({
              key,
              value: node.tags[key],
            } as Tag)
        )
        .sort((a, b) => a.key.localeCompare(b.key))
    },
    mapDuration(duration: SummaryComponentDependencyDuration) {
      if (!duration) {
        return undefined
      }

      return {
        min: this.microsecondsToMilliseconds(duration.min),
        max: this.microsecondsToMilliseconds(duration.max),
        p50: this.microsecondsToMilliseconds(duration.p50),
        p90: this.microsecondsToMilliseconds(duration.p90),
        p99: this.microsecondsToMilliseconds(duration.p99),
        p99Point9: this.microsecondsToMilliseconds(duration.p99Point9),
      } as Duration
    },
    microsecondsToMilliseconds(microseconds: number): number {
      return Math.round(microseconds / 1000)
    },
  },
})
</script>
