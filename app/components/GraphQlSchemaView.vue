<template>
  <div class="graphql-schema">Loading...</div>
</template>

<style scoped>
.graphql-schema {
  background-color: #FFF;
}
</style>

<script lang="ts">
import Vue, { PropType } from 'vue'
import {MetaInfo} from "vue-meta";
import { buildSchema, getIntrospectionQuery, graphqlSync } from "graphql";
import { Component, GraphQlSchema } from '~/types/kronicle-service'

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
    this.load(this.component.graphQlSchemas[this.graphQlSchemaIndex - 1])
  },
  methods: {
    load(graphQlSchema: GraphQlSchema) {
      const schemaIdl = JSON.parse(graphQlSchema.schema)
      const introspection = graphqlSync({
        schema: buildSchema(schemaIdl),
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
