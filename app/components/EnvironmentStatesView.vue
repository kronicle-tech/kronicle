<template>
  <div>
    <ComponentFilters
      :environment-id-filter-enabled="true"
      :plugin-id-filter-enabled="true"
    />

    <b-alert v-if="items.length === 0" show variant="info">
      This page is empty as there are currently no environment status items to show on this page
    </b-alert>

    <b-card-group v-if="items.length > 0" columns>
      <template v-for="item in items">
        <template v-if="item.itemType === 'check'">
          <b-card :key="item.key" :class="`border-${item.statusVariant}`">
            <h5>{{ item.environment.id }} <span class="text-muted">//</span> {{ item.plugin.id }} <span class="text-muted">//</span> {{ item.component.name }}</h5>
            <h4 :class="`text-${item.statusVariant}`">
              <b-avatar v-if="item.firstCheck.avatarUrl" :src="item.firstCheck.avatarUrl" />
              <span v-for="(check, checkIndex) in item.allChecks" :key="checkIndex">
                <span v-if="checkIndex > 0"><br></span>
                {{ check.name }}
                <b-badge>{{ check.description }}</b-badge>
              </span>
            </h4>

            <div><span class="text-muted">Status:</span> <b-badge :variant="item.statusVariant">{{ item.firstCheck.status }}</b-badge></div>
            <div><span class="text-muted">Status Message:</span> <b>{{ item.firstCheck.statusMessage }}</b></div>
            <div><span class="text-muted">Updated At:</span> <b><FormattedDateTime :value="item.firstCheck.updateTimestamp" /></b></div>

            <div
              v-for="link in item.allLinks"
              :key="link.link.url"
              class="mt-2"
            >
              <b-link
                :href="link.link.url"
                target="_blank"
                class="card-link"
              >
                {{ getLinkDescription(link) }} <b-icon icon="box-arrow-up-right" aria-label="opens in new window" />
              </b-link>
            </div>
          </b-card>
        </template>
        <template v-if="item.itemType === 'log-summary'">
          <b-card :key="item.key">
            <h5>
              {{ item.environment.id }} <span class="text-muted">//</span> {{ item.component.name }} <span class="text-muted">//</span> {{ item.plugin.id }} <span class="text-muted">//</span> Logs
            </h5>
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
          </b-card>
          <b-card v-for="level in item.logSummary.levels.filter(level => level.topMessages.length > 0)" :key="`${item.key}-${level.level}`">
            <h5>
              {{ item.environment.id }} <span class="text-muted">//</span> {{ item.component.name }} <span class="text-muted">//</span> {{ item.plugin.id }} <span class="text-muted">//</span> Logs
            </h5>
            <h4>{{ level.level || '{blank}' }} - Top Messages - {{ item.logSummary.name }}</h4>

            <ol>
              <li v-for="topMessage in level.topMessages" :key="topMessage.message">
                <span class="text-muted">{{ topMessage.message || '{blank}' }}</span> <b class="h5">x<FormattedNumber :value="topMessage.count"/></b><br>
              </li>
            </ol>

            <div class="mt-2"><span class="text-muted">Updated At:</span> <b><FormattedDateTime :value="item.logSummary.updateTimestamp" /></b></div>
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
  BLink
} from 'bootstrap-vue'
import {
  CheckState,
  Component,
  ComponentStateCheckStatus,
  EnvironmentPluginState,
  EnvironmentState, Link, LogSummaryState,
} from '~/types/kronicle-service'
import ComponentFilters from '~/components/ComponentFilters.vue'
import FormattedDateTime from "~/components/FormattedDateTime.vue";
import FormattedNumber from "~/components/FormattedNumber.vue";

type ItemType = 'check' | 'log-summary'
const ITEM_TYPE_ORDER: ItemType[] = ['check', 'log-summary']
const CHECK_STATUS_ORDER: ComponentStateCheckStatus[] =
  ["critical", "warning", "pending", "unknown", "ok"]

interface BaseItem {
  readonly itemType: ItemType;
  readonly key: string;
  readonly component: Component;
  readonly environment: EnvironmentState;
  readonly plugin: EnvironmentPluginState;
}

interface LinkAndCheck {
  readonly link: Link;
  readonly check: CheckState;
}

interface CheckItem extends BaseItem {
  readonly itemType: 'check';
  readonly classifier: string;
  readonly firstCheck: CheckState;
  readonly allChecks: CheckState[];
  readonly allLinks: LinkAndCheck[];
  readonly statusVariant: string;
}

interface LogSummaryItem extends BaseItem {
  readonly itemType: 'log-summary';
  readonly logSummary: LogSummaryState;
}

type Item = CheckItem | LogSummaryItem;

interface ClassifierIndex {
  readonly classifier: string;
  readonly index: number;
}

export default Vue.extend({
  components: {
    'b-avatar': BAvatar,
    'b-card': BCard,
    'b-card-group': BCardGroup,
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
      const items = components.flatMap(component => that.mapComponent(component));
      const classifierIndexes = new Map<string, ClassifierIndex>()
      const groupedItems: Item[] = []
      items.forEach(item => {
        if (item.itemType === 'check') {
          let classifierIndex = classifierIndexes.get(item.classifier);
          if (classifierIndex) {
            const existingItem = groupedItems[classifierIndex.index] as CheckItem;
            existingItem.allChecks.push(item.firstCheck);
            existingItem.allLinks.push(...item.firstCheck.links.map(link => ({ link, check: item.firstCheck })));
          } else {
            classifierIndex = {
              classifier: item.classifier,
              index: groupedItems.length,
            };
            classifierIndexes.set(item.classifier, classifierIndex);
            groupedItems.push(item);
          }
        } else {
          groupedItems.push(item);
        }
      });
      return groupedItems.sort(that.compareItems);
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
        classifier: `${component.id}_${environment.id}_${plugin.id}_${check.avatarUrl}_${check.status}_${check.statusMessage}_${check.updateTimestamp}`,
        component,
        environment,
        plugin,
        firstCheck: check,
        allChecks: [check],
        allLinks: check.links.map(link => ({ link, check })),
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
          return "primary";
        case "unknown":
          return "info";
        case "warning":
          return "warning";
      }
    },
    compareItems(a: Item, b: Item) {
      let result: number
      result = ITEM_TYPE_ORDER.indexOf(a.itemType) - ITEM_TYPE_ORDER.indexOf(b.itemType)
      if (result !== 0) {
        return result;
      }
      if (a.itemType === 'check' && b.itemType === 'check') {
        result = CHECK_STATUS_ORDER.indexOf(a.firstCheck.status) - CHECK_STATUS_ORDER.indexOf(b.firstCheck.status)
        if (result !== 0) {
          return result;
        }
      }
      result = a.environment.id.localeCompare(b.environment.id)
      if (result !== 0) {
        return result;
      }
      result = a.component.id.localeCompare(b.component.id)
      if (result !== 0) {
        return result;
      }
      return 0;
    },
    getLinkDescription(linkAndCheck: LinkAndCheck): string {
      if (linkAndCheck.link.description) {
        return `${linkAndCheck.check.name}: ${linkAndCheck.link.description}`
      } else {
        return `${linkAndCheck.check.name}: ${linkAndCheck.link.url}`
      }
    },
  },
})
</script>
