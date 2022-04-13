<template>
  <div>
    <b-container fluid class="mt-3">
      <b-row>
        <b-col>
          <b-card-group columns>
            <template v-for="item in items">
              <template v-if="item.itemType === 'check'">
                <b-card :key="item.key" :class="`border-${item.statusClass}`">
                  <b-card-title>
                    <span class="h5">{{ item.environment.id }} <span class="text-muted">//</span> {{ item.plugin.id }} <span class="text-muted">//</span> {{ item.component.name }}<br><br></span>
                    {{ item.check.name }} <b-badge>{{ item.check.description }}</b-badge>
                  </b-card-title>
                  <b-card-text>
                    <span class="text-muted">Status:</span> <b>{{ item.check.status }}</b><br>
                    <span class="text-muted">Status Message:</span> <b>{{ item.check.statusMessage }}</b><br>
                    <span class="text-muted">Updated At:</span> <b><FormattedDateTime :value="item.check.updateTimestamp" /></b><br>
                  </b-card-text>
                </b-card>
              </template>
              <template v-if="item.itemType === 'log-summary'">
                <b-card :key="item.key">
                  <b-card-title class="h5">
                    {{ item.environment.id }} <span class="text-muted">//</span> {{ item.component.name }} <span class="text-muted">//</span> {{ item.plugin.id }} <span class="text-muted">//</span> Logs
                  </b-card-title>
                  <b-card-text>
                    <h4>{{ item.logSummary.name }}</h4>

                    <p>
                      <span v-for="level in item.logSummary.levels" :key="level.level">
                        <span class="text-muted">{{ level.level || '{blank}' }}:</span> <b><FormattedNumber :value="level.count"/></b><br>
                      </span>
                    </p>

                    <div v-for="comparison in item.logSummary.comparisons" :key="comparison.name">
                      <h5>{{ comparison.name }}</h5>

                      <p>
                        <span v-for="level in comparison.levels" :key="level.level">
                          <span class="text-muted">{{ level.level || '{blank}' }}:</span> <b><FormattedNumber :value="level.count"/></b><br>
                        </span>
                      </p>
                    </div>
                  </b-card-text>
                </b-card>
                <b-card v-for="level in item.logSummary.levels" :key="`${item.key}-${level.level}`">
                  <b-card-title class="h5">
                    {{ item.environment.id }} <span class="text-muted">//</span> {{ item.component.name }} <span class="text-muted">//</span> {{ item.plugin.id }} <span class="text-muted">//</span> Logs
                  </b-card-title>
                  <b-card-text>
                    <h4> {{ level.level }} - Top Messages</h4>

                    <ol>
                      <li v-for="topMessage in level.topMessages" :key="topMessage.message">
                        <span class="text-muted">{{ topMessage.message || '{blank}' }}</span> <b>x<FormattedNumber :value="topMessage.count"/></b><br>
                      </li>
                    </ol>
                  </b-card-text>
                </b-card>
              </template>
            </template>
          </b-card-group>
        </b-col>
        <b-col md="3">
          <ComponentFilters :components="components"/>
        </b-col>
      </b-row>
    </b-container>
  </div>
</template>

<script lang="ts">
import Vue, {PropType} from 'vue'
import {BCard, BCardBody, BCardGroup, BCardText, BCardTitle, BCol, BContainer, BRow} from 'bootstrap-vue'
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
  readonly statusClass: string;
}

interface LogSummaryItem extends BaseItem {
  readonly itemType: 'log-summary';
  readonly logSummary: LogSummaryState;
}

type Item = CheckItem | LogSummaryItem;

export default Vue.extend({
  components: {
    'b-card': BCard,
    'b-card-group': BCardGroup,
    'b-card-text': BCardText,
    'b-card-title': BCardTitle,
    'b-col': BCol,
    'b-container': BContainer,
    'b-row': BRow,
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
      if (!component.state) {
        return [];
      }
      const that = this;
      return component.state.environments.flatMap(environment => that.mapEnvironment(component, environment));
    },
    mapEnvironment(component: Component, environment: EnvironmentState): Item[] {
      const that = this;
      return environment.plugins.flatMap(plugin => that.mapPlugin(component, environment, plugin));
    },
    mapPlugin(component: Component, environment: EnvironmentState, plugin: EnvironmentPluginState): Item[] {
      const that = this;
      return [].concat(
        plugin.checks.map(check => that.mapCheck(component, environment, plugin, check)),
        plugin.logSummaries.map(logSummary => that.mapLogSummary(component, environment, plugin, logSummary)),
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
        statusClass: that.getStatusClass(check.status),
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
    getStatusClass(status: ComponentStateCheckStatus): string {
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
