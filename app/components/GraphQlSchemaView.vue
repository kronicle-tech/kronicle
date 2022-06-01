<template>
  <div class="graphql-schema">Loading...</div>
</template>

<style>
.graphql-schema {
  color: #000;
  background-color: #FFF;
}

.graphql-schema .loading-box {
  position: fixed;
  left: 0;
  right: 0;
  top: 0;
  bottom: 0;
}

.graphql-schema .graphql-voyager {
  min-height: 100vh;
}

.graphql-schema .menu-content {
  position: fixed;
}
</style>

<script lang="ts">
import Vue, {PropType} from 'vue'
import {MetaInfo} from "vue-meta";
import {buildSchema, getIntrospectionQuery, graphqlSync} from "graphql";
import {Component, GraphQlSchema, GraphQlSchemasState} from '~/types/kronicle-service'
import {findComponentState} from "~/src/componentStateUtils";

export default Vue.extend({
  props: {
    component: {
      type: Object as PropType<Component>,
      required: true,
    },
    graphQlSchemaIndex: {
      type: Number,
      required: true,
    },
  },
  head(): MetaInfo {
    return {
      link: [
        {
          rel: 'stylesheet',
          href: 'https://cdn.jsdelivr.net/npm/graphql-voyager/dist/voyager.css',
        },
      ],
      script: [
        {
          src: 'https://cdn.jsdelivr.net/npm/react@16/umd/react.production.min.js',
        },
        {
          src: 'https://cdn.jsdelivr.net/npm/react-dom@16/umd/react-dom.production.min.js',
        },
        {
          src: 'https://cdn.jsdelivr.net/npm/graphql-voyager/dist/voyager.min.js',
        }
      ],
    }
  },
  mounted() {
    const graphQlSchemas: GraphQlSchemasState | undefined = findComponentState(this.component, 'graphql-schemas')
    this.load(graphQlSchemas!.graphQlSchemas[this.graphQlSchemaIndex - 1])
  },
  methods: {
    load(graphQlSchema: GraphQlSchema) {
      const introspection = graphqlSync({
        schema: buildSchema(graphQlSchema.schema),
        source: getIntrospectionQuery()
      })
      const windowAny = window as any
      windowAny.GraphQLVoyager.init(this.$el, {
        introspection,
      });
    },
  },
})
</script>
