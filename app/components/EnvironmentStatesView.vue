<template>
  <div>
    <ComponentFilters :components="components" environment-id-filter-enabled />

    <b-card-group columns>
      <template v-for="item in items">
        <template v-if="item.itemType === 'check'">
          <b-card :key="item.key" :class="`border-${item.statusVariant}`">
            <b-card-title>
              <h5>{{ item.environment.id }} <span class="text-muted">//</span> {{ item.plugin.id }} <span class="text-muted">//</span> {{ item.component.name }}</h5>
              <h4 :class="`text-${item.statusVariant}`">
                <b-avatar v-if="item.check.avatarUrl" :src="item.check.avatarUrl" />
                {{ item.check.name }}
                <b-badge>{{ item.check.description }}</b-badge>
              </h4>
            </b-card-title>
            <b-card-text>
              <div><span class="text-muted">Status:</span> <b-badge :variant="item.statusVariant">{{ item.check.status }}</b-badge></div>
              <div><span class="text-muted">Status Message:</span> <b>{{ item.check.statusMessage }}</b></div>
              <div><span class="text-muted">Updated At:</span> <b><FormattedDateTime :value="item.check.updateTimestamp" /></b></div>

              <div v-if="item.check.links" class="mt-1">
                <b-link v-for="link in item.check.links" :key="link.url" :href="link.url" class="card-link">{{ link.description || link.url }}</b-link>
              </div>
            </b-card-text>
          </b-card>
        </template>
        <template v-if="item.itemType === 'log-summary'">
          <b-card :key="item.key">
            <b-card-title class="h5">
              {{ item.environment.id }} <span class="text-muted">//</span> {{ item.component.name }} <span class="text-muted">//</span> {{ item.plugin.id }} <span class="text-muted">//</span> Logs
            </b-card-title>
            <b-card-text>
              <h4 class="text-info">{{ item.logSummary.name }}</h4>

              <div v-for="level in item.logSummary.levels" :key="level.level">
                <span class="text-muted">{{ level.level || '{blank}' }}:</span> <b><FormattedNumber :value="level.count"/></b>
              </div>

              <div v-for="comparison in item.logSummary.comparisons" :key="comparison.name" class="mt-2">
                <h5 class="text-info">{{ comparison.name }}</h5>

                <div v-for="level in comparison.levels" :key="level.level">
                  <span class="text-muted">{{ level.level || '{blank}' }}:</span> <b><FormattedNumber :value="level.count"/></b>
                </div>
              </div>

              <div class="mt-2"><span class="text-muted">Updated At:</span> <b><FormattedDateTime :value="item.logSummary.updateTimestamp" /></b></div>
            </b-card-text>
          </b-card>
          <b-card v-for="level in item.logSummary.levels.filter(level => level.topMessages.length > 0)" :key="`${item.key}-${level.level}`">
            <b-card-title class="h5">
              {{ item.environment.id }} <span class="text-muted">//</span> {{ item.component.name }} <span class="text-muted">//</span> {{ item.plugin.id }} <span class="text-muted">//</span> Logs
            </b-card-title>
            <b-card-text>
              <h4> {{ level.level || '{blank}' }} - Top Messages - {{ item.logSummary.name }}</h4>

              <ol>
                <li v-for="topMessage in level.topMessages" :key="topMessage.message">
                  <span class="text-muted">{{ topMessage.message || '{blank}' }}</span> <b class="h5">x<FormattedNumber :value="topMessage.count"/></b><br>
                </li>
              </ol>

              <div class="mt-2"><span class="text-muted">Updated At:</span> <b><FormattedDateTime :value="item.logSummary.updateTimestamp" /></b></div>
            </b-card-text>
          </b-card>
        </template>
      </template>
    </b-card-group>
  </div>
</template>

<script lang="ts">
import Vue, {PropType} from 'vue'
import {
  BAvatar,
  BCard,
  BCardGroup,
  BCardText,
  BCardTitle,
  BLink
} from 'bootstrap-vue'
import {
  CheckState,
  Component,
  ComponentStateCheckStatus,
  EnvironmentPluginState,
  EnvironmentState, LogSummaryState,
} from '~/types/kronicle-service'
import ComponentFilters from '~/components/ComponentFilters.vue'
import FormattedDateTime from "~/components/FormattedDateTime.vue";
import FormattedNumber from "~/components/FormattedNumber.vue";

interface BaseItem {
  readonly itemType: string;
  readonly key: string;
  readonly component: Component;
  readonly environment: EnvironmentState;
  readonly plugin: EnvironmentPluginState;
}

interface CheckItem extends BaseItem {
  readonly itemType: 'check';
  readonly check: CheckState;
  readonly statusVariant: string;
}

interface LogSummaryItem extends BaseItem {
  readonly itemType: 'log-summary';
  readonly logSummary: LogSummaryState;
}

type Item = CheckItem | LogSummaryItem;

export default Vue.extend({
  components: {
    'b-avatar': BAvatar,
    'b-card': BCard,
    'b-card-group': BCardGroup,
    'b-card-text': BCardText,
    'b-card-title': BCardTitle,
    'b-link': BLink,
    ComponentFilters,
    FormattedDateTime,
    FormattedNumber,
  },
  props: {
    components: {
      type: Array as PropType<Component[]>,
      default: undefined,
    },
  },
  computed: {
    filteredComponents(): Component[] {
      return this.$store.state.componentFilters.filteredComponents
    },
    items(): Item[] {
      return this.mapComponents(this.filteredComponents)
    },
    componentCount(): number {
      return this.filteredComponents.length
    },
  },
  methods: {
    mapComponents(components: Component[]): Item[] {
      const that = this;
      return components.flatMap(component => that.mapComponent(component))
        .sort((a, b) => {
          let result = a.environment.id.localeCompare(b.environment.id)
          if (result !== 0) {
            return result;
          }
          result = a.component.id.localeCompare(b.component.id)
          if (result !== 0) {
            return result;
          }
          result = a.itemType.localeCompare(b.itemType)
          if (result !== 0) {
            return result;
          }
          return 0;
        });
    },
    mapComponent(component: Component): Item[] {
      if (!component.state || !component.state.environments) {
        return [];
      }
      const that = this;
      return component.state.environments.flatMap(environment => that.mapEnvironment(component, environment));
    },
    mapEnvironment(component: Component, environment: EnvironmentState): Item[] {
      if (!environment.plugins) {
        return [];
      }
      const that = this;
      return environment.plugins.flatMap(plugin => that.mapPlugin(component, environment, plugin));
    },
    mapPlugin(component: Component, environment: EnvironmentState, plugin: EnvironmentPluginState): Item[] {
      const that = this;
      return ([] as Item[]).concat(
        (plugin.checks ?? []).map(check => that.mapCheck(component, environment, plugin, check)),
        (plugin.logSummaries ?? []).map(logSummary => that.mapLogSummary(component, environment, plugin, logSummary)),
      );
    },
    mapCheck(component: Component, environment: EnvironmentState, plugin: EnvironmentPluginState, check: CheckState): Item {
      const that = this;
      return {
        itemType: 'check',
        key: `${component.id}_${environment.id}_${plugin.id}_${check.name}`,
        component,
        environment,
        plugin,
        check,
        statusVariant: that.getStatusVariant(check.status),
      };
    },
    mapLogSummary(component: Component, environment: EnvironmentState, plugin: EnvironmentPluginState, logSummary: LogSummaryState): Item {
      return {
        itemType: 'log-summary',
        key: `${component.id}_${environment.id}_${plugin.id}_${logSummary.name}`,
        component,
        environment,
        plugin,
        logSummary,
      };
    },
    getStatusVariant(status: ComponentStateCheckStatus): string {
      switch (status) {
        case "critical":
          return "danger";
        case "ok":
          return "success";
        case "pending":
          return "muted";
        case "unknown":
          return "info";
        case "warning":
          return "warning";
      }
    }
  },
})
</script>
