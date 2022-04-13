<template>
  <div>
    <b-container fluid>
      <b-row>
        <b-col>
          <b-card-group columns>
            <b-card v-for="item in items" :key="item.key" :class="`border-${item.statusClass}`">
              <b-card-text>
                {{ item.environment.id }} <span class="text-muted">//</span> {{ item.component.name }} <span class="text-muted">//</span> {{ item.plugin.id }} <span class="text-muted">//</span> {{ item.check.name }}<br>
                <br>

                <span class="text-muted">Description:</span> <b>{{ item.check.description }}</b><br>
                <span class="text-muted">Status:</span> <b>{{ item.check.status }}</b><br>
                <span class="text-muted">Status Message:</span> <b>{{ item.check.statusMessage }}</b><br>
                <span class="text-muted">Updated At:</span> <b>{{ item.check.updateTimestamp }}</b><br>
              </b-card-text>
            </b-card>
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
import {BCard, BCardGroup, BCardText, BCol, BContainer, BRow} from 'bootstrap-vue'
import {
  CheckState,
  Component,
  ComponentStateCheckStatus,
  EnvironmentPluginState,
  EnvironmentState,
} from '~/types/kronicle-service'
import ComponentFilters from '~/components/ComponentFilters.vue'

interface Item {
  readonly key: string;
  readonly component: Component;
  readonly environment: EnvironmentState;
  readonly plugin: EnvironmentPluginState;
  readonly check: CheckState;
  readonly statusClass: string;
}

export default Vue.extend({
  components: {
    'b-card': BCard,
    'b-card-group': BCardGroup,
    'b-card-text': BCardText,
    'b-col': BCol,
    'b-container': BContainer,
    'b-row': BRow,
    ComponentFilters,
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
      const that = this;
      return that.filteredComponents.flatMap(component =>
        (component.state?.environments ?? []).flatMap(environment =>
          (environment.plugins ?? []).flatMap(plugin => plugin.checks.map(check =>
            ({
        key: `${component.id}_${environment.id}_${plugin.id}_${check.name}`,
        component,
        environment,
        plugin,
        check,
        statusClass: that.getStatusClass(check.status),
      })))))
    },
    componentCount(): number {
      return this.filteredComponents.length
    },
  },
  methods: {
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
