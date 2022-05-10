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
      const schema1 = JSON.parse(graphQlSchema.schema)
      const schema2 = JSON.parse(`{
  "data" : {
    "__schema" : {
      "queryType" : {
        "name" : "Query"
      },
      "types" : [ {
        "kind" : "OBJECT",
        "name" : "Alias",
        "fields" : [ {
          "name" : "description",
          "args" : [ ],
          "type" : {
            "kind" : "SCALAR",
            "name" : "String"
          },
          "isDeprecated" : false
        }, {
          "name" : "id",
          "args" : [ ],
          "type" : {
            "kind" : "SCALAR",
            "name" : "String"
          },
          "isDeprecated" : false
        }, {
          "name" : "notes",
          "args" : [ ],
          "type" : {
            "kind" : "SCALAR",
            "name" : "String"
          },
          "isDeprecated" : false
        } ],
        "interfaces" : [ ]
      }, {
        "kind" : "OBJECT",
        "name" : "Area",
        "fields" : [ {
          "name" : "components",
          "args" : [ ],
          "type" : {
            "kind" : "LIST",
            "ofType" : {
              "kind" : "OBJECT",
              "name" : "Component"
            }
          },
          "isDeprecated" : false
        }, {
          "name" : "description",
          "args" : [ ],
          "type" : {
            "kind" : "SCALAR",
            "name" : "String"
          },
          "isDeprecated" : false
        }, {
          "name" : "id",
          "args" : [ ],
          "type" : {
            "kind" : "SCALAR",
            "name" : "String"
          },
          "isDeprecated" : false
        }, {
          "name" : "links",
          "args" : [ ],
          "type" : {
            "kind" : "LIST",
            "ofType" : {
              "kind" : "OBJECT",
              "name" : "Link"
            }
          },
          "isDeprecated" : false
        }, {
          "name" : "name",
          "args" : [ ],
          "type" : {
            "kind" : "SCALAR",
            "name" : "String"
          },
          "isDeprecated" : false
        }, {
          "name" : "notes",
          "args" : [ ],
          "type" : {
            "kind" : "SCALAR",
            "name" : "String"
          },
          "isDeprecated" : false
        }, {
          "name" : "tags",
          "args" : [ ],
          "type" : {
            "kind" : "LIST",
            "ofType" : {
              "kind" : "SCALAR",
              "name" : "String"
            }
          },
          "isDeprecated" : false
        }, {
          "name" : "teams",
          "args" : [ ],
          "type" : {
            "kind" : "LIST",
            "ofType" : {
              "kind" : "OBJECT",
              "name" : "Team"
            }
          },
          "isDeprecated" : false
        } ],
        "interfaces" : [ ]
      }, {
        "kind" : "OBJECT",
        "name" : "AreasOutput",
        "fields" : [ {
          "name" : "areas",
          "args" : [ ],
          "type" : {
            "kind" : "NON_NULL",
            "ofType" : {
              "kind" : "LIST",
              "ofType" : {
                "kind" : "OBJECT",
                "name" : "Area"
              }
            }
          },
          "isDeprecated" : false
        } ],
        "interfaces" : [ ]
      }, {
        "kind" : "SCALAR",
        "name" : "Boolean",
        "description" : "Built-in Boolean"
      }, {
        "kind" : "OBJECT",
        "name" : "CheckState",
        "fields" : [ {
          "name" : "avatarUrl",
          "args" : [ ],
          "type" : {
            "kind" : "SCALAR",
            "name" : "String"
          },
          "isDeprecated" : false
        }, {
          "name" : "description",
          "args" : [ ],
          "type" : {
            "kind" : "SCALAR",
            "name" : "String"
          },
          "isDeprecated" : false
        }, {
          "name" : "links",
          "args" : [ ],
          "type" : {
            "kind" : "LIST",
            "ofType" : {
              "kind" : "OBJECT",
              "name" : "Link"
            }
          },
          "isDeprecated" : false
        }, {
          "name" : "name",
          "args" : [ ],
          "type" : {
            "kind" : "NON_NULL",
            "ofType" : {
              "kind" : "SCALAR",
              "name" : "String"
            }
          },
          "isDeprecated" : false
        }, {
          "name" : "status",
          "args" : [ ],
          "type" : {
            "kind" : "NON_NULL",
            "ofType" : {
              "kind" : "SCALAR",
              "name" : "String"
            }
          },
          "isDeprecated" : false
        }, {
          "name" : "statusMessage",
          "args" : [ ],
          "type" : {
            "kind" : "NON_NULL",
            "ofType" : {
              "kind" : "SCALAR",
              "name" : "String"
            }
          },
          "isDeprecated" : false
        }, {
          "name" : "updateTimestamp",
          "args" : [ ],
          "type" : {
            "kind" : "NON_NULL",
            "ofType" : {
              "kind" : "SCALAR",
              "name" : "String"
            }
          },
          "isDeprecated" : false
        } ],
        "interfaces" : [ ]
      }, {
        "kind" : "OBJECT",
        "name" : "Component",
        "fields" : [ {
          "name" : "aliases",
          "args" : [ ],
          "type" : {
            "kind" : "LIST",
            "ofType" : {
              "kind" : "OBJECT",
              "name" : "Alias"
            }
          },
          "isDeprecated" : false
        }, {
          "name" : "crossFunctionalRequirements",
          "args" : [ ],
          "type" : {
            "kind" : "LIST",
            "ofType" : {
              "kind" : "OBJECT",
              "name" : "CrossFunctionalRequirement"
            }
          },
          "isDeprecated" : false
        }, {
          "name" : "dependencies",
          "args" : [ ],
          "type" : {
            "kind" : "LIST",
            "ofType" : {
              "kind" : "OBJECT",
              "name" : "ComponentDependency"
            }
          },
          "isDeprecated" : false
        }, {
          "name" : "description",
          "args" : [ ],
          "type" : {
            "kind" : "SCALAR",
            "name" : "String"
          },
          "isDeprecated" : false
        }, {
          "name" : "discovered",
          "args" : [ ],
          "type" : {
            "kind" : "SCALAR",
            "name" : "Boolean"
          },
          "isDeprecated" : false
        }, {
          "name" : "gitRepo",
          "args" : [ ],
          "type" : {
            "kind" : "OBJECT",
            "name" : "GitRepo"
          },
          "isDeprecated" : false
        }, {
          "name" : "gradle",
          "args" : [ ],
          "type" : {
            "kind" : "OBJECT",
            "name" : "Gradle"
          },
          "isDeprecated" : false
        }, {
          "name" : "graphQlSchemas",
          "args" : [ ],
          "type" : {
            "kind" : "LIST",
            "ofType" : {
              "kind" : "OBJECT",
              "name" : "GraphQlSchema"
            }
          },
          "isDeprecated" : false
        }, {
          "name" : "id",
          "args" : [ ],
          "type" : {
            "kind" : "SCALAR",
            "name" : "String"
          },
          "isDeprecated" : false
        }, {
          "name" : "imports",
          "args" : [ ],
          "type" : {
            "kind" : "LIST",
            "ofType" : {
              "kind" : "OBJECT",
              "name" : "Import"
            }
          },
          "isDeprecated" : false
        }, {
          "name" : "keySoftware",
          "args" : [ ],
          "type" : {
            "kind" : "LIST",
            "ofType" : {
              "kind" : "OBJECT",
              "name" : "KeySoftware"
            }
          },
          "isDeprecated" : false
        }, {
          "name" : "linesOfCode",
          "args" : [ ],
          "type" : {
            "kind" : "OBJECT",
            "name" : "LinesOfCode"
          },
          "isDeprecated" : false
        }, {
          "name" : "links",
          "args" : [ ],
          "type" : {
            "kind" : "LIST",
            "ofType" : {
              "kind" : "OBJECT",
              "name" : "Link"
            }
          },
          "isDeprecated" : false
        }, {
          "name" : "name",
          "args" : [ ],
          "type" : {
            "kind" : "SCALAR",
            "name" : "String"
          },
          "isDeprecated" : false
        }, {
          "name" : "nodeJs",
          "args" : [ ],
          "type" : {
            "kind" : "OBJECT",
            "name" : "NodeJs"
          },
          "isDeprecated" : false
        }, {
          "name" : "notes",
          "args" : [ ],
          "type" : {
            "kind" : "SCALAR",
            "name" : "String"
          },
          "isDeprecated" : false
        }, {
          "name" : "openApiSpecs",
          "args" : [ ],
          "type" : {
            "kind" : "LIST",
            "ofType" : {
              "kind" : "OBJECT",
              "name" : "OpenApiSpec"
            }
          },
          "isDeprecated" : false
        }, {
          "name" : "platformId",
          "args" : [ ],
          "type" : {
            "kind" : "SCALAR",
            "name" : "String"
          },
          "isDeprecated" : false
        }, {
          "name" : "readme",
          "args" : [ ],
          "type" : {
            "kind" : "OBJECT",
            "name" : "Readme"
          },
          "isDeprecated" : false
        }, {
          "name" : "repo",
          "args" : [ ],
          "type" : {
            "kind" : "OBJECT",
            "name" : "RepoReference"
          },
          "isDeprecated" : false
        }, {
          "name" : "responsibilities",
          "args" : [ ],
          "type" : {
            "kind" : "LIST",
            "ofType" : {
              "kind" : "OBJECT",
              "name" : "Responsibility"
            }
          },
          "isDeprecated" : false
        }, {
          "name" : "scannerErrors",
          "args" : [ ],
          "type" : {
            "kind" : "LIST",
            "ofType" : {
              "kind" : "OBJECT",
              "name" : "ScannerError"
            }
          },
          "isDeprecated" : false
        }, {
          "name" : "software",
          "args" : [ ],
          "type" : {
            "kind" : "LIST",
            "ofType" : {
              "kind" : "OBJECT",
              "name" : "Software"
            }
          },
          "isDeprecated" : false
        }, {
          "name" : "softwareRepositories",
          "args" : [ ],
          "type" : {
            "kind" : "LIST",
            "ofType" : {
              "kind" : "OBJECT",
              "name" : "SoftwareRepository"
            }
          },
          "isDeprecated" : false
        }, {
          "name" : "sonarQubeProjects",
          "args" : [ ],
          "type" : {
            "kind" : "LIST",
            "ofType" : {
              "kind" : "OBJECT",
              "name" : "SonarQubeProject"
            }
          },
          "isDeprecated" : false
        }, {
          "name" : "state",
          "args" : [ ],
          "type" : {
            "kind" : "OBJECT",
            "name" : "ComponentState"
          },
          "isDeprecated" : false
        }, {
          "name" : "tags",
          "args" : [ ],
          "type" : {
            "kind" : "LIST",
            "ofType" : {
              "kind" : "OBJECT",
              "name" : "Tag"
            }
          },
          "isDeprecated" : false
        }, {
          "name" : "teams",
          "args" : [ ],
          "type" : {
            "kind" : "LIST",
            "ofType" : {
              "kind" : "OBJECT",
              "name" : "ComponentTeam"
            }
          },
          "isDeprecated" : false
        }, {
          "name" : "techDebts",
          "args" : [ ],
          "type" : {
            "kind" : "LIST",
            "ofType" : {
              "kind" : "OBJECT",
              "name" : "TechDebt"
            }
          },
          "isDeprecated" : false
        }, {
          "name" : "testResults",
          "args" : [ ],
          "type" : {
            "kind" : "LIST",
            "ofType" : {
              "kind" : "OBJECT",
              "name" : "TestResult"
            }
          },
          "isDeprecated" : false
        }, {
          "name" : "toDos",
          "args" : [ ],
          "type" : {
            "kind" : "LIST",
            "ofType" : {
              "kind" : "OBJECT",
              "name" : "ToDo"
            }
          },
          "isDeprecated" : false
        }, {
          "name" : "typeId",
          "args" : [ ],
          "type" : {
            "kind" : "SCALAR",
            "name" : "String"
          },
          "isDeprecated" : false
        }, {
          "name" : "zipkin",
          "args" : [ ],
          "type" : {
            "kind" : "OBJECT",
            "name" : "Zipkin"
          },
          "isDeprecated" : false
        } ],
        "interfaces" : [ ]
      }, {
        "kind" : "OBJECT",
        "name" : "ComponentDependency",
        "fields" : [ {
          "name" : "description",
          "args" : [ ],
          "type" : {
            "kind" : "SCALAR",
            "name" : "String"
          },
          "isDeprecated" : false
        }, {
          "name" : "direction",
          "args" : [ ],
          "type" : {
            "kind" : "SCALAR",
            "name" : "String"
          },
          "isDeprecated" : false
        }, {
          "name" : "label",
          "args" : [ ],
          "type" : {
            "kind" : "SCALAR",
            "name" : "String"
          },
          "isDeprecated" : false
        }, {
          "name" : "targetComponentId",
          "args" : [ ],
          "type" : {
            "kind" : "SCALAR",
            "name" : "String"
          },
          "isDeprecated" : false
        }, {
          "name" : "typeId",
          "args" : [ ],
          "type" : {
            "kind" : "SCALAR",
            "name" : "String"
          },
          "isDeprecated" : false
        } ],
        "interfaces" : [ ]
      }, {
        "kind" : "OBJECT",
        "name" : "ComponentState",
        "fields" : [ {
          "name" : "environments",
          "args" : [ ],
          "type" : {
            "kind" : "LIST",
            "ofType" : {
              "kind" : "OBJECT",
              "name" : "EnvironmentState"
            }
          },
          "isDeprecated" : false
        } ],
        "interfaces" : [ ]
      }, {
        "kind" : "OBJECT",
        "name" : "ComponentTeam",
        "fields" : [ {
          "name" : "description",
          "args" : [ ],
          "type" : {
            "kind" : "SCALAR",
            "name" : "String"
          },
          "isDeprecated" : false
        }, {
          "name" : "teamId",
          "args" : [ ],
          "type" : {
            "kind" : "SCALAR",
            "name" : "String"
          },
          "isDeprecated" : false
        }, {
          "name" : "type",
          "args" : [ ],
          "type" : {
            "kind" : "SCALAR",
            "name" : "String"
          },
          "isDeprecated" : false
        } ],
        "interfaces" : [ ]
      }, {
        "kind" : "OBJECT",
        "name" : "ComponentsOutput",
        "fields" : [ {
          "name" : "components",
          "args" : [ ],
          "type" : {
            "kind" : "NON_NULL",
            "ofType" : {
              "kind" : "LIST",
              "ofType" : {
                "kind" : "OBJECT",
                "name" : "Component"
              }
            }
          },
          "isDeprecated" : false
        } ],
        "interfaces" : [ ]
      }, {
        "kind" : "OBJECT",
        "name" : "CrossFunctionalRequirement",
        "fields" : [ {
          "name" : "description",
          "args" : [ ],
          "type" : {
            "kind" : "SCALAR",
            "name" : "String"
          },
          "isDeprecated" : false
        }, {
          "name" : "links",
          "args" : [ ],
          "type" : {
            "kind" : "LIST",
            "ofType" : {
              "kind" : "OBJECT",
              "name" : "Link"
            }
          },
          "isDeprecated" : false
        }, {
          "name" : "notes",
          "args" : [ ],
          "type" : {
            "kind" : "SCALAR",
            "name" : "String"
          },
          "isDeprecated" : false
        } ],
        "interfaces" : [ ]
      }, {
        "kind" : "OBJECT",
        "name" : "EnvironmentPluginState",
        "fields" : [ {
          "name" : "checks",
          "args" : [ ],
          "type" : {
            "kind" : "LIST",
            "ofType" : {
              "kind" : "OBJECT",
              "name" : "CheckState"
            }
          },
          "isDeprecated" : false
        }, {
          "name" : "id",
          "args" : [ ],
          "type" : {
            "kind" : "NON_NULL",
            "ofType" : {
              "kind" : "SCALAR",
              "name" : "String"
            }
          },
          "isDeprecated" : false
        }, {
          "name" : "logSummaries",
          "args" : [ ],
          "type" : {
            "kind" : "LIST",
            "ofType" : {
              "kind" : "OBJECT",
              "name" : "LogSummaryState"
            }
          },
          "isDeprecated" : false
        } ],
        "interfaces" : [ ]
      }, {
        "kind" : "OBJECT",
        "name" : "EnvironmentState",
        "fields" : [ {
          "name" : "id",
          "args" : [ ],
          "type" : {
            "kind" : "NON_NULL",
            "ofType" : {
              "kind" : "SCALAR",
              "name" : "String"
            }
          },
          "isDeprecated" : false
        }, {
          "name" : "plugins",
          "args" : [ ],
          "type" : {
            "kind" : "LIST",
            "ofType" : {
              "kind" : "OBJECT",
              "name" : "EnvironmentPluginState"
            }
          },
          "isDeprecated" : false
        } ],
        "interfaces" : [ ]
      }, {
        "kind" : "OBJECT",
        "name" : "FileExtensionCount",
        "fields" : [ {
          "name" : "count",
          "args" : [ ],
          "type" : {
            "kind" : "NON_NULL",
            "ofType" : {
              "kind" : "SCALAR",
              "name" : "Int"
            }
          },
          "isDeprecated" : false
        }, {
          "name" : "fileExtension",
          "args" : [ ],
          "type" : {
            "kind" : "NON_NULL",
            "ofType" : {
              "kind" : "SCALAR",
              "name" : "String"
            }
          },
          "isDeprecated" : false
        } ],
        "interfaces" : [ ]
      }, {
        "kind" : "OBJECT",
        "name" : "GitRepo",
        "fields" : [ {
          "name" : "authorCount",
          "args" : [ ],
          "type" : {
            "kind" : "SCALAR",
            "name" : "Int"
          },
          "isDeprecated" : false
        }, {
          "name" : "authors",
          "args" : [ ],
          "type" : {
            "kind" : "LIST",
            "ofType" : {
              "kind" : "OBJECT",
              "name" : "Identity"
            }
          },
          "isDeprecated" : false
        }, {
          "name" : "commitCount",
          "args" : [ ],
          "type" : {
            "kind" : "SCALAR",
            "name" : "Int"
          },
          "isDeprecated" : false
        }, {
          "name" : "committerCount",
          "args" : [ ],
          "type" : {
            "kind" : "SCALAR",
            "name" : "Int"
          },
          "isDeprecated" : false
        }, {
          "name" : "committers",
          "args" : [ ],
          "type" : {
            "kind" : "LIST",
            "ofType" : {
              "kind" : "OBJECT",
              "name" : "Identity"
            }
          },
          "isDeprecated" : false
        }, {
          "name" : "firstCommitTimestamp",
          "args" : [ ],
          "type" : {
            "kind" : "SCALAR",
            "name" : "String"
          },
          "isDeprecated" : false
        }, {
          "name" : "lastCommitTimestamp",
          "args" : [ ],
          "type" : {
            "kind" : "SCALAR",
            "name" : "String"
          },
          "isDeprecated" : false
        } ],
        "interfaces" : [ ]
      }, {
        "kind" : "OBJECT",
        "name" : "Gradle",
        "fields" : [ {
          "name" : "used",
          "args" : [ ],
          "type" : {
            "kind" : "SCALAR",
            "name" : "Boolean"
          },
          "isDeprecated" : false
        } ],
        "interfaces" : [ ]
      }, {
        "kind" : "OBJECT",
        "name" : "GraphQlSchema",
        "fields" : [ {
          "name" : "description",
          "args" : [ ],
          "type" : {
            "kind" : "SCALAR",
            "name" : "String"
          },
          "isDeprecated" : false
        }, {
          "name" : "file",
          "args" : [ ],
          "type" : {
            "kind" : "SCALAR",
            "name" : "String"
          },
          "isDeprecated" : false
        }, {
          "name" : "schema",
          "args" : [ ],
          "type" : {
            "kind" : "SCALAR",
            "name" : "String"
          },
          "isDeprecated" : false
        }, {
          "name" : "url",
          "args" : [ ],
          "type" : {
            "kind" : "SCALAR",
            "name" : "String"
          },
          "isDeprecated" : false
        } ],
        "interfaces" : [ ]
      }, {
        "kind" : "OBJECT",
        "name" : "Identity",
        "fields" : [ {
          "name" : "commitCount",
          "args" : [ ],
          "type" : {
            "kind" : "SCALAR",
            "name" : "Int"
          },
          "isDeprecated" : false
        }, {
          "name" : "emailAddress",
          "args" : [ ],
          "type" : {
            "kind" : "SCALAR",
            "name" : "String"
          },
          "isDeprecated" : false
        }, {
          "name" : "firstCommitTimestamp",
          "args" : [ ],
          "type" : {
            "kind" : "SCALAR",
            "name" : "String"
          },
          "isDeprecated" : false
        }, {
          "name" : "lastCommitTimestamp",
          "args" : [ ],
          "type" : {
            "kind" : "SCALAR",
            "name" : "String"
          },
          "isDeprecated" : false
        }, {
          "name" : "names",
          "args" : [ ],
          "type" : {
            "kind" : "LIST",
            "ofType" : {
              "kind" : "SCALAR",
              "name" : "String"
            }
          },
          "isDeprecated" : false
        } ],
        "interfaces" : [ ]
      }, {
        "kind" : "OBJECT",
        "name" : "Import",
        "fields" : [ {
          "name" : "name",
          "args" : [ ],
          "type" : {
            "kind" : "SCALAR",
            "name" : "String"
          },
          "isDeprecated" : false
        }, {
          "name" : "scannerId",
          "args" : [ ],
          "type" : {
            "kind" : "SCALAR",
            "name" : "String"
          },
          "isDeprecated" : false
        }, {
          "name" : "type",
          "args" : [ ],
          "type" : {
            "kind" : "NON_NULL",
            "ofType" : {
              "kind" : "SCALAR",
              "name" : "String"
            }
          },
          "isDeprecated" : false
        } ],
        "interfaces" : [ ]
      }, {
        "kind" : "SCALAR",
        "name" : "Int",
        "description" : "Built-in Int"
      }, {
        "kind" : "OBJECT",
        "name" : "KeySoftware",
        "fields" : [ {
          "name" : "name",
          "args" : [ ],
          "type" : {
            "kind" : "SCALAR",
            "name" : "String"
          },
          "isDeprecated" : false
        }, {
          "name" : "versions",
          "args" : [ ],
          "type" : {
            "kind" : "NON_NULL",
            "ofType" : {
              "kind" : "LIST",
              "ofType" : {
                "kind" : "SCALAR",
                "name" : "String"
              }
            }
          },
          "isDeprecated" : false
        } ],
        "interfaces" : [ ]
      }, {
        "kind" : "OBJECT",
        "name" : "LinesOfCode",
        "fields" : [ {
          "name" : "count",
          "args" : [ ],
          "type" : {
            "kind" : "SCALAR",
            "name" : "Int"
          },
          "isDeprecated" : false
        }, {
          "name" : "fileExtensionCounts",
          "args" : [ ],
          "type" : {
            "kind" : "LIST",
            "ofType" : {
              "kind" : "OBJECT",
              "name" : "FileExtensionCount"
            }
          },
          "isDeprecated" : false
        } ],
        "interfaces" : [ ]
      }, {
        "kind" : "OBJECT",
        "name" : "Link",
        "fields" : [ {
          "name" : "description",
          "args" : [ ],
          "type" : {
            "kind" : "SCALAR",
            "name" : "String"
          },
          "isDeprecated" : false
        }, {
          "name" : "url",
          "args" : [ ],
          "type" : {
            "kind" : "SCALAR",
            "name" : "String"
          },
          "isDeprecated" : false
        } ],
        "interfaces" : [ ]
      }, {
        "kind" : "OBJECT",
        "name" : "LogLevelState",
        "fields" : [ {
          "name" : "count",
          "args" : [ ],
          "type" : {
            "kind" : "NON_NULL",
            "ofType" : {
              "kind" : "SCALAR",
              "name" : "Int"
            }
          },
          "isDeprecated" : false
        }, {
          "name" : "level",
          "args" : [ ],
          "type" : {
            "kind" : "SCALAR",
            "name" : "String"
          },
          "isDeprecated" : false
        }, {
          "name" : "topMessages",
          "args" : [ ],
          "type" : {
            "kind" : "LIST",
            "ofType" : {
              "kind" : "OBJECT",
              "name" : "LogMessageState"
            }
          },
          "isDeprecated" : false
        } ],
        "interfaces" : [ ]
      }, {
        "kind" : "OBJECT",
        "name" : "LogMessageState",
        "fields" : [ {
          "name" : "count",
          "args" : [ ],
          "type" : {
            "kind" : "NON_NULL",
            "ofType" : {
              "kind" : "SCALAR",
              "name" : "Int"
            }
          },
          "isDeprecated" : false
        }, {
          "name" : "message",
          "args" : [ ],
          "type" : {
            "kind" : "SCALAR",
            "name" : "String"
          },
          "isDeprecated" : false
        } ],
        "interfaces" : [ ]
      }, {
        "kind" : "OBJECT",
        "name" : "LogSummaryState",
        "fields" : [ {
          "name" : "comparisons",
          "args" : [ ],
          "type" : {
            "kind" : "LIST",
            "ofType" : {
              "kind" : "OBJECT",
              "name" : "LogSummaryState"
            }
          },
          "isDeprecated" : false
        }, {
          "name" : "endTimestamp",
          "args" : [ ],
          "type" : {
            "kind" : "NON_NULL",
            "ofType" : {
              "kind" : "SCALAR",
              "name" : "String"
            }
          },
          "isDeprecated" : false
        }, {
          "name" : "levels",
          "args" : [ ],
          "type" : {
            "kind" : "LIST",
            "ofType" : {
              "kind" : "OBJECT",
              "name" : "LogLevelState"
            }
          },
          "isDeprecated" : false
        }, {
          "name" : "name",
          "args" : [ ],
          "type" : {
            "kind" : "NON_NULL",
            "ofType" : {
              "kind" : "SCALAR",
              "name" : "String"
            }
          },
          "isDeprecated" : false
        }, {
          "name" : "startTimestamp",
          "args" : [ ],
          "type" : {
            "kind" : "NON_NULL",
            "ofType" : {
              "kind" : "SCALAR",
              "name" : "String"
            }
          },
          "isDeprecated" : false
        }, {
          "name" : "updateTimestamp",
          "args" : [ ],
          "type" : {
            "kind" : "NON_NULL",
            "ofType" : {
              "kind" : "SCALAR",
              "name" : "String"
            }
          },
          "isDeprecated" : false
        } ],
        "interfaces" : [ ]
      }, {
        "kind" : "OBJECT",
        "name" : "NodeJs",
        "fields" : [ {
          "name" : "used",
          "args" : [ ],
          "type" : {
            "kind" : "SCALAR",
            "name" : "Boolean"
          },
          "isDeprecated" : false
        } ],
        "interfaces" : [ ]
      }, {
        "kind" : "OBJECT",
        "name" : "OpenApiSpec",
        "fields" : [ {
          "name" : "description",
          "args" : [ ],
          "type" : {
            "kind" : "SCALAR",
            "name" : "String"
          },
          "isDeprecated" : false
        }, {
          "name" : "file",
          "args" : [ ],
          "type" : {
            "kind" : "SCALAR",
            "name" : "String"
          },
          "isDeprecated" : false
        }, {
          "name" : "scannerId",
          "args" : [ ],
          "type" : {
            "kind" : "SCALAR",
            "name" : "String"
          },
          "isDeprecated" : false
        }, {
          "name" : "spec",
          "args" : [ ],
          "type" : {
            "kind" : "SCALAR",
            "name" : "String"
          },
          "isDeprecated" : false
        }, {
          "name" : "url",
          "args" : [ ],
          "type" : {
            "kind" : "SCALAR",
            "name" : "String"
          },
          "isDeprecated" : false
        } ],
        "interfaces" : [ ]
      }, {
        "kind" : "OBJECT",
        "name" : "Query",
        "fields" : [ {
          "name" : "areas",
          "args" : [ ],
          "type" : {
            "kind" : "OBJECT",
            "name" : "AreasOutput"
          },
          "isDeprecated" : false
        }, {
          "name" : "components",
          "args" : [ ],
          "type" : {
            "kind" : "OBJECT",
            "name" : "ComponentsOutput"
          },
          "isDeprecated" : false
        }, {
          "name" : "teams",
          "args" : [ ],
          "type" : {
            "kind" : "OBJECT",
            "name" : "TeamsOutput"
          },
          "isDeprecated" : false
        } ],
        "interfaces" : [ ]
      }, {
        "kind" : "OBJECT",
        "name" : "Readme",
        "fields" : [ {
          "name" : "content",
          "args" : [ ],
          "type" : {
            "kind" : "SCALAR",
            "name" : "String"
          },
          "isDeprecated" : false
        }, {
          "name" : "fileName",
          "args" : [ ],
          "type" : {
            "kind" : "SCALAR",
            "name" : "String"
          },
          "isDeprecated" : false
        } ],
        "interfaces" : [ ]
      }, {
        "kind" : "OBJECT",
        "name" : "RepoReference",
        "fields" : [ {
          "name" : "url",
          "args" : [ ],
          "type" : {
            "kind" : "SCALAR",
            "name" : "String"
          },
          "isDeprecated" : false
        } ],
        "interfaces" : [ ]
      }, {
        "kind" : "OBJECT",
        "name" : "Responsibility",
        "fields" : [ {
          "name" : "description",
          "args" : [ ],
          "type" : {
            "kind" : "SCALAR",
            "name" : "String"
          },
          "isDeprecated" : false
        } ],
        "interfaces" : [ ]
      }, {
        "kind" : "OBJECT",
        "name" : "ScannerError",
        "fields" : [ {
          "name" : "cause",
          "args" : [ ],
          "type" : {
            "kind" : "OBJECT",
            "name" : "ScannerError"
          },
          "isDeprecated" : false
        }, {
          "name" : "message",
          "args" : [ ],
          "type" : {
            "kind" : "SCALAR",
            "name" : "String"
          },
          "isDeprecated" : false
        }, {
          "name" : "scannerId",
          "args" : [ ],
          "type" : {
            "kind" : "SCALAR",
            "name" : "String"
          },
          "isDeprecated" : false
        } ],
        "interfaces" : [ ]
      }, {
        "kind" : "OBJECT",
        "name" : "Software",
        "fields" : [ {
          "name" : "dependencyType",
          "args" : [ ],
          "type" : {
            "kind" : "NON_NULL",
            "ofType" : {
              "kind" : "SCALAR",
              "name" : "String"
            }
          },
          "isDeprecated" : false
        }, {
          "name" : "name",
          "args" : [ ],
          "type" : {
            "kind" : "SCALAR",
            "name" : "String"
          },
          "isDeprecated" : false
        }, {
          "name" : "packaging",
          "args" : [ ],
          "type" : {
            "kind" : "SCALAR",
            "name" : "String"
          },
          "isDeprecated" : false
        }, {
          "name" : "scannerId",
          "args" : [ ],
          "type" : {
            "kind" : "SCALAR",
            "name" : "String"
          },
          "isDeprecated" : false
        }, {
          "name" : "scope",
          "args" : [ ],
          "type" : {
            "kind" : "SCALAR",
            "name" : "String"
          },
          "isDeprecated" : false
        }, {
          "name" : "type",
          "args" : [ ],
          "type" : {
            "kind" : "NON_NULL",
            "ofType" : {
              "kind" : "SCALAR",
              "name" : "String"
            }
          },
          "isDeprecated" : false
        }, {
          "name" : "version",
          "args" : [ ],
          "type" : {
            "kind" : "SCALAR",
            "name" : "String"
          },
          "isDeprecated" : false
        }, {
          "name" : "versionSelector",
          "args" : [ ],
          "type" : {
            "kind" : "SCALAR",
            "name" : "String"
          },
          "isDeprecated" : false
        } ],
        "interfaces" : [ ]
      }, {
        "kind" : "OBJECT",
        "name" : "SoftwareRepository",
        "fields" : [ {
          "name" : "safe",
          "args" : [ ],
          "type" : {
            "kind" : "NON_NULL",
            "ofType" : {
              "kind" : "SCALAR",
              "name" : "Boolean"
            }
          },
          "isDeprecated" : false
        }, {
          "name" : "scannerId",
          "args" : [ ],
          "type" : {
            "kind" : "SCALAR",
            "name" : "String"
          },
          "isDeprecated" : false
        }, {
          "name" : "scope",
          "args" : [ ],
          "type" : {
            "kind" : "SCALAR",
            "name" : "String"
          },
          "isDeprecated" : false
        }, {
          "name" : "type",
          "args" : [ ],
          "type" : {
            "kind" : "NON_NULL",
            "ofType" : {
              "kind" : "SCALAR",
              "name" : "String"
            }
          },
          "isDeprecated" : false
        }, {
          "name" : "url",
          "args" : [ ],
          "type" : {
            "kind" : "SCALAR",
            "name" : "String"
          },
          "isDeprecated" : false
        } ],
        "interfaces" : [ ]
      }, {
        "kind" : "OBJECT",
        "name" : "SonarQubeMeasure",
        "fields" : [ {
          "name" : "bestValue",
          "args" : [ ],
          "type" : {
            "kind" : "SCALAR",
            "name" : "Boolean"
          },
          "isDeprecated" : false
        }, {
          "name" : "metric",
          "args" : [ ],
          "type" : {
            "kind" : "SCALAR",
            "name" : "String"
          },
          "isDeprecated" : false
        }, {
          "name" : "value",
          "args" : [ ],
          "type" : {
            "kind" : "SCALAR",
            "name" : "String"
          },
          "isDeprecated" : false
        } ],
        "interfaces" : [ ]
      }, {
        "kind" : "OBJECT",
        "name" : "SonarQubeProject",
        "fields" : [ {
          "name" : "key",
          "args" : [ ],
          "type" : {
            "kind" : "SCALAR",
            "name" : "String"
          },
          "isDeprecated" : false
        }, {
          "name" : "lastCommitTimestamp",
          "args" : [ ],
          "type" : {
            "kind" : "SCALAR",
            "name" : "String"
          },
          "isDeprecated" : false
        }, {
          "name" : "measures",
          "args" : [ ],
          "type" : {
            "kind" : "LIST",
            "ofType" : {
              "kind" : "OBJECT",
              "name" : "SonarQubeMeasure"
            }
          },
          "isDeprecated" : false
        }, {
          "name" : "name",
          "args" : [ ],
          "type" : {
            "kind" : "SCALAR",
            "name" : "String"
          },
          "isDeprecated" : false
        }, {
          "name" : "url",
          "args" : [ ],
          "type" : {
            "kind" : "SCALAR",
            "name" : "String"
          },
          "isDeprecated" : false
        } ],
        "interfaces" : [ ]
      }, {
        "kind" : "SCALAR",
        "name" : "String",
        "description" : "Built-in String"
      }, {
        "kind" : "OBJECT",
        "name" : "Tag",
        "fields" : [ {
          "name" : "key",
          "args" : [ ],
          "type" : {
            "kind" : "SCALAR",
            "name" : "String"
          },
          "isDeprecated" : false
        }, {
          "name" : "value",
          "args" : [ ],
          "type" : {
            "kind" : "SCALAR",
            "name" : "String"
          },
          "isDeprecated" : false
        } ],
        "interfaces" : [ ]
      }, {
        "kind" : "OBJECT",
        "name" : "Team",
        "fields" : [ {
          "name" : "areaId",
          "args" : [ ],
          "type" : {
            "kind" : "SCALAR",
            "name" : "String"
          },
          "isDeprecated" : false
        }, {
          "name" : "components",
          "args" : [ ],
          "type" : {
            "kind" : "LIST",
            "ofType" : {
              "kind" : "OBJECT",
              "name" : "Component"
            }
          },
          "isDeprecated" : false
        }, {
          "name" : "description",
          "args" : [ ],
          "type" : {
            "kind" : "SCALAR",
            "name" : "String"
          },
          "isDeprecated" : false
        }, {
          "name" : "emailAddress",
          "args" : [ ],
          "type" : {
            "kind" : "SCALAR",
            "name" : "String"
          },
          "isDeprecated" : false
        }, {
          "name" : "id",
          "args" : [ ],
          "type" : {
            "kind" : "SCALAR",
            "name" : "String"
          },
          "isDeprecated" : false
        }, {
          "name" : "links",
          "args" : [ ],
          "type" : {
            "kind" : "LIST",
            "ofType" : {
              "kind" : "OBJECT",
              "name" : "Link"
            }
          },
          "isDeprecated" : false
        }, {
          "name" : "name",
          "args" : [ ],
          "type" : {
            "kind" : "SCALAR",
            "name" : "String"
          },
          "isDeprecated" : false
        }, {
          "name" : "notes",
          "args" : [ ],
          "type" : {
            "kind" : "SCALAR",
            "name" : "String"
          },
          "isDeprecated" : false
        }, {
          "name" : "tags",
          "args" : [ ],
          "type" : {
            "kind" : "LIST",
            "ofType" : {
              "kind" : "SCALAR",
              "name" : "String"
            }
          },
          "isDeprecated" : false
        } ],
        "interfaces" : [ ]
      }, {
        "kind" : "OBJECT",
        "name" : "TeamsOutput",
        "fields" : [ {
          "name" : "teams",
          "args" : [ ],
          "type" : {
            "kind" : "NON_NULL",
            "ofType" : {
              "kind" : "LIST",
              "ofType" : {
                "kind" : "OBJECT",
                "name" : "Team"
              }
            }
          },
          "isDeprecated" : false
        } ],
        "interfaces" : [ ]
      }, {
        "kind" : "OBJECT",
        "name" : "TechDebt",
        "fields" : [ {
          "name" : "description",
          "args" : [ ],
          "type" : {
            "kind" : "SCALAR",
            "name" : "String"
          },
          "isDeprecated" : false
        }, {
          "name" : "links",
          "args" : [ ],
          "type" : {
            "kind" : "LIST",
            "ofType" : {
              "kind" : "OBJECT",
              "name" : "Link"
            }
          },
          "isDeprecated" : false
        }, {
          "name" : "notes",
          "args" : [ ],
          "type" : {
            "kind" : "SCALAR",
            "name" : "String"
          },
          "isDeprecated" : false
        }, {
          "name" : "priority",
          "args" : [ ],
          "type" : {
            "kind" : "SCALAR",
            "name" : "String"
          },
          "isDeprecated" : false
        } ],
        "interfaces" : [ ]
      }, {
        "kind" : "OBJECT",
        "name" : "TestResult",
        "fields" : [ {
          "name" : "message",
          "args" : [ ],
          "type" : {
            "kind" : "SCALAR",
            "name" : "String"
          },
          "isDeprecated" : false
        }, {
          "name" : "outcome",
          "args" : [ ],
          "type" : {
            "kind" : "NON_NULL",
            "ofType" : {
              "kind" : "SCALAR",
              "name" : "String"
            }
          },
          "isDeprecated" : false
        }, {
          "name" : "priority",
          "args" : [ ],
          "type" : {
            "kind" : "NON_NULL",
            "ofType" : {
              "kind" : "SCALAR",
              "name" : "String"
            }
          },
          "isDeprecated" : false
        }, {
          "name" : "testId",
          "args" : [ ],
          "type" : {
            "kind" : "SCALAR",
            "name" : "String"
          },
          "isDeprecated" : false
        } ],
        "interfaces" : [ ]
      }, {
        "kind" : "OBJECT",
        "name" : "ToDo",
        "fields" : [ {
          "name" : "description",
          "args" : [ ],
          "type" : {
            "kind" : "SCALAR",
            "name" : "String"
          },
          "isDeprecated" : false
        }, {
          "name" : "file",
          "args" : [ ],
          "type" : {
            "kind" : "SCALAR",
            "name" : "String"
          },
          "isDeprecated" : false
        } ],
        "interfaces" : [ ]
      }, {
        "kind" : "OBJECT",
        "name" : "Zipkin",
        "fields" : [ {
          "name" : "downstream",
          "args" : [ ],
          "type" : {
            "kind" : "LIST",
            "ofType" : {
              "kind" : "OBJECT",
              "name" : "ZipkinDependency"
            }
          },
          "isDeprecated" : false
        }, {
          "name" : "serviceName",
          "args" : [ ],
          "type" : {
            "kind" : "SCALAR",
            "name" : "String"
          },
          "isDeprecated" : false
        }, {
          "name" : "upstream",
          "args" : [ ],
          "type" : {
            "kind" : "LIST",
            "ofType" : {
              "kind" : "OBJECT",
              "name" : "ZipkinDependency"
            }
          },
          "isDeprecated" : false
        }, {
          "name" : "used",
          "args" : [ ],
          "type" : {
            "kind" : "SCALAR",
            "name" : "Boolean"
          },
          "isDeprecated" : false
        } ],
        "interfaces" : [ ]
      }, {
        "kind" : "OBJECT",
        "name" : "ZipkinDependency",
        "fields" : [ {
          "name" : "callCount",
          "args" : [ ],
          "type" : {
            "kind" : "SCALAR",
            "name" : "Int"
          },
          "isDeprecated" : false
        }, {
          "name" : "child",
          "args" : [ ],
          "type" : {
            "kind" : "SCALAR",
            "name" : "String"
          },
          "isDeprecated" : false
        }, {
          "name" : "errorCount",
          "args" : [ ],
          "type" : {
            "kind" : "SCALAR",
            "name" : "Int"
          },
          "isDeprecated" : false
        }, {
          "name" : "parent",
          "args" : [ ],
          "type" : {
            "kind" : "SCALAR",
            "name" : "String"
          },
          "isDeprecated" : false
        } ],
        "interfaces" : [ ]
      }, {
        "kind" : "OBJECT",
        "name" : "__Directive",
        "fields" : [ {
          "name" : "name",
          "description" : "The __Directive type represents a Directive that a server supports.",
          "args" : [ ],
          "type" : {
            "kind" : "NON_NULL",
            "ofType" : {
              "kind" : "SCALAR",
              "name" : "String"
            }
          },
          "isDeprecated" : false
        }, {
          "name" : "description",
          "args" : [ ],
          "type" : {
            "kind" : "SCALAR",
            "name" : "String"
          },
          "isDeprecated" : false
        }, {
          "name" : "isRepeatable",
          "args" : [ ],
          "type" : {
            "kind" : "NON_NULL",
            "ofType" : {
              "kind" : "SCALAR",
              "name" : "Boolean"
            }
          },
          "isDeprecated" : false
        }, {
          "name" : "locations",
          "args" : [ ],
          "type" : {
            "kind" : "NON_NULL",
            "ofType" : {
              "kind" : "LIST",
              "ofType" : {
                "kind" : "NON_NULL",
                "ofType" : {
                  "kind" : "ENUM",
                  "name" : "__DirectiveLocation"
                }
              }
            }
          },
          "isDeprecated" : false
        }, {
          "name" : "args",
          "args" : [ ],
          "type" : {
            "kind" : "NON_NULL",
            "ofType" : {
              "kind" : "LIST",
              "ofType" : {
                "kind" : "NON_NULL",
                "ofType" : {
                  "kind" : "OBJECT",
                  "name" : "__InputValue"
                }
              }
            }
          },
          "isDeprecated" : false
        }, {
          "name" : "onOperation",
          "args" : [ ],
          "type" : {
            "kind" : "SCALAR",
            "name" : "Boolean"
          },
          "isDeprecated" : true,
          "deprecationReason" : "Use \`locations\`."
        }, {
          "name" : "onFragment",
          "args" : [ ],
          "type" : {
            "kind" : "SCALAR",
            "name" : "Boolean"
          },
          "isDeprecated" : true,
          "deprecationReason" : "Use \`locations\`."
        }, {
          "name" : "onField",
          "args" : [ ],
          "type" : {
            "kind" : "SCALAR",
            "name" : "Boolean"
          },
          "isDeprecated" : true,
          "deprecationReason" : "Use \`locations\`."
        } ],
        "interfaces" : [ ]
      }, {
        "kind" : "ENUM",
        "name" : "__DirectiveLocation",
        "description" : "An enum describing valid locations where a directive can be placed",
        "enumValues" : [ {
          "name" : "QUERY",
          "description" : "Indicates the directive is valid on queries.",
          "isDeprecated" : false
        }, {
          "name" : "MUTATION",
          "description" : "Indicates the directive is valid on mutations.",
          "isDeprecated" : false
        }, {
          "name" : "SUBSCRIPTION",
          "description" : "Indicates the directive is valid on subscriptions.",
          "isDeprecated" : false
        }, {
          "name" : "FIELD",
          "description" : "Indicates the directive is valid on fields.",
          "isDeprecated" : false
        }, {
          "name" : "FRAGMENT_DEFINITION",
          "description" : "Indicates the directive is valid on fragment definitions.",
          "isDeprecated" : false
        }, {
          "name" : "FRAGMENT_SPREAD",
          "description" : "Indicates the directive is valid on fragment spreads.",
          "isDeprecated" : false
        }, {
          "name" : "INLINE_FRAGMENT",
          "description" : "Indicates the directive is valid on inline fragments.",
          "isDeprecated" : false
        }, {
          "name" : "VARIABLE_DEFINITION",
          "description" : "Indicates the directive is valid on variable definitions.",
          "isDeprecated" : false
        }, {
          "name" : "SCHEMA",
          "description" : "Indicates the directive is valid on a schema SDL definition.",
          "isDeprecated" : false
        }, {
          "name" : "SCALAR",
          "description" : "Indicates the directive is valid on a scalar SDL definition.",
          "isDeprecated" : false
        }, {
          "name" : "OBJECT",
          "description" : "Indicates the directive is valid on an object SDL definition.",
          "isDeprecated" : false
        }, {
          "name" : "FIELD_DEFINITION",
          "description" : "Indicates the directive is valid on a field SDL definition.",
          "isDeprecated" : false
        }, {
          "name" : "ARGUMENT_DEFINITION",
          "description" : "Indicates the directive is valid on a field argument SDL definition.",
          "isDeprecated" : false
        }, {
          "name" : "INTERFACE",
          "description" : "Indicates the directive is valid on an interface SDL definition.",
          "isDeprecated" : false
        }, {
          "name" : "UNION",
          "description" : "Indicates the directive is valid on an union SDL definition.",
          "isDeprecated" : false
        }, {
          "name" : "ENUM",
          "description" : "Indicates the directive is valid on an enum SDL definition.",
          "isDeprecated" : false
        }, {
          "name" : "ENUM_VALUE",
          "description" : "Indicates the directive is valid on an enum value SDL definition.",
          "isDeprecated" : false
        }, {
          "name" : "INPUT_OBJECT",
          "description" : "Indicates the directive is valid on an input object SDL definition.",
          "isDeprecated" : false
        }, {
          "name" : "INPUT_FIELD_DEFINITION",
          "description" : "Indicates the directive is valid on an input object field SDL definition.",
          "isDeprecated" : false
        } ]
      }, {
        "kind" : "OBJECT",
        "name" : "__EnumValue",
        "fields" : [ {
          "name" : "name",
          "args" : [ ],
          "type" : {
            "kind" : "NON_NULL",
            "ofType" : {
              "kind" : "SCALAR",
              "name" : "String"
            }
          },
          "isDeprecated" : false
        }, {
          "name" : "description",
          "args" : [ ],
          "type" : {
            "kind" : "SCALAR",
            "name" : "String"
          },
          "isDeprecated" : false
        }, {
          "name" : "isDeprecated",
          "args" : [ ],
          "type" : {
            "kind" : "NON_NULL",
            "ofType" : {
              "kind" : "SCALAR",
              "name" : "Boolean"
            }
          },
          "isDeprecated" : false
        }, {
          "name" : "deprecationReason",
          "args" : [ ],
          "type" : {
            "kind" : "SCALAR",
            "name" : "String"
          },
          "isDeprecated" : false
        } ],
        "interfaces" : [ ]
      }, {
        "kind" : "OBJECT",
        "name" : "__Field",
        "fields" : [ {
          "name" : "name",
          "args" : [ ],
          "type" : {
            "kind" : "NON_NULL",
            "ofType" : {
              "kind" : "SCALAR",
              "name" : "String"
            }
          },
          "isDeprecated" : false
        }, {
          "name" : "description",
          "args" : [ ],
          "type" : {
            "kind" : "SCALAR",
            "name" : "String"
          },
          "isDeprecated" : false
        }, {
          "name" : "args",
          "args" : [ {
            "name" : "includeDeprecated",
            "type" : {
              "kind" : "SCALAR",
              "name" : "Boolean"
            },
            "defaultValue" : "false"
          } ],
          "type" : {
            "kind" : "NON_NULL",
            "ofType" : {
              "kind" : "LIST",
              "ofType" : {
                "kind" : "NON_NULL",
                "ofType" : {
                  "kind" : "OBJECT",
                  "name" : "__InputValue"
                }
              }
            }
          },
          "isDeprecated" : false
        }, {
          "name" : "type",
          "args" : [ ],
          "type" : {
            "kind" : "NON_NULL",
            "ofType" : {
              "kind" : "OBJECT",
              "name" : "__Type"
            }
          },
          "isDeprecated" : false
        }, {
          "name" : "isDeprecated",
          "args" : [ ],
          "type" : {
            "kind" : "NON_NULL",
            "ofType" : {
              "kind" : "SCALAR",
              "name" : "Boolean"
            }
          },
          "isDeprecated" : false
        }, {
          "name" : "deprecationReason",
          "args" : [ ],
          "type" : {
            "kind" : "SCALAR",
            "name" : "String"
          },
          "isDeprecated" : false
        } ],
        "interfaces" : [ ]
      }, {
        "kind" : "OBJECT",
        "name" : "__InputValue",
        "fields" : [ {
          "name" : "name",
          "args" : [ ],
          "type" : {
            "kind" : "NON_NULL",
            "ofType" : {
              "kind" : "SCALAR",
              "name" : "String"
            }
          },
          "isDeprecated" : false
        }, {
          "name" : "description",
          "args" : [ ],
          "type" : {
            "kind" : "SCALAR",
            "name" : "String"
          },
          "isDeprecated" : false
        }, {
          "name" : "type",
          "args" : [ ],
          "type" : {
            "kind" : "NON_NULL",
            "ofType" : {
              "kind" : "OBJECT",
              "name" : "__Type"
            }
          },
          "isDeprecated" : false
        }, {
          "name" : "defaultValue",
          "args" : [ ],
          "type" : {
            "kind" : "SCALAR",
            "name" : "String"
          },
          "isDeprecated" : false
        }, {
          "name" : "isDeprecated",
          "args" : [ ],
          "type" : {
            "kind" : "SCALAR",
            "name" : "Boolean"
          },
          "isDeprecated" : false
        }, {
          "name" : "deprecationReason",
          "args" : [ ],
          "type" : {
            "kind" : "SCALAR",
            "name" : "String"
          },
          "isDeprecated" : false
        } ],
        "interfaces" : [ ]
      }, {
        "kind" : "OBJECT",
        "name" : "__Schema",
        "description" : "A GraphQL Introspection defines the capabilities of a GraphQL server. It exposes all available types and directives on the server, the entry points for query, mutation, and subscription operations.",
        "fields" : [ {
          "name" : "description",
          "args" : [ ],
          "type" : {
            "kind" : "SCALAR",
            "name" : "String"
          },
          "isDeprecated" : false
        }, {
          "name" : "types",
          "description" : "A list of all types supported by this server.",
          "args" : [ ],
          "type" : {
            "kind" : "NON_NULL",
            "ofType" : {
              "kind" : "LIST",
              "ofType" : {
                "kind" : "NON_NULL",
                "ofType" : {
                  "kind" : "OBJECT",
                  "name" : "__Type"
                }
              }
            }
          },
          "isDeprecated" : false
        }, {
          "name" : "queryType",
          "description" : "The type that query operations will be rooted at.",
          "args" : [ ],
          "type" : {
            "kind" : "NON_NULL",
            "ofType" : {
              "kind" : "OBJECT",
              "name" : "__Type"
            }
          },
          "isDeprecated" : false
        }, {
          "name" : "mutationType",
          "description" : "If this server supports mutation, the type that mutation operations will be rooted at.",
          "args" : [ ],
          "type" : {
            "kind" : "OBJECT",
            "name" : "__Type"
          },
          "isDeprecated" : false
        }, {
          "name" : "directives",
          "description" : "'A list of all directives supported by this server.",
          "args" : [ ],
          "type" : {
            "kind" : "NON_NULL",
            "ofType" : {
              "kind" : "LIST",
              "ofType" : {
                "kind" : "NON_NULL",
                "ofType" : {
                  "kind" : "OBJECT",
                  "name" : "__Directive"
                }
              }
            }
          },
          "isDeprecated" : false
        }, {
          "name" : "subscriptionType",
          "description" : "'If this server support subscription, the type that subscription operations will be rooted at.",
          "args" : [ ],
          "type" : {
            "kind" : "OBJECT",
            "name" : "__Type"
          },
          "isDeprecated" : false
        } ],
        "interfaces" : [ ]
      }, {
        "kind" : "OBJECT",
        "name" : "__Type",
        "fields" : [ {
          "name" : "kind",
          "args" : [ ],
          "type" : {
            "kind" : "NON_NULL",
            "ofType" : {
              "kind" : "ENUM",
              "name" : "__TypeKind"
            }
          },
          "isDeprecated" : false
        }, {
          "name" : "name",
          "args" : [ ],
          "type" : {
            "kind" : "SCALAR",
            "name" : "String"
          },
          "isDeprecated" : false
        }, {
          "name" : "description",
          "args" : [ ],
          "type" : {
            "kind" : "SCALAR",
            "name" : "String"
          },
          "isDeprecated" : false
        }, {
          "name" : "fields",
          "args" : [ {
            "name" : "includeDeprecated",
            "type" : {
              "kind" : "SCALAR",
              "name" : "Boolean"
            },
            "defaultValue" : "false"
          } ],
          "type" : {
            "kind" : "LIST",
            "ofType" : {
              "kind" : "NON_NULL",
              "ofType" : {
                "kind" : "OBJECT",
                "name" : "__Field"
              }
            }
          },
          "isDeprecated" : false
        }, {
          "name" : "interfaces",
          "args" : [ ],
          "type" : {
            "kind" : "LIST",
            "ofType" : {
              "kind" : "NON_NULL",
              "ofType" : {
                "kind" : "OBJECT",
                "name" : "__Type"
              }
            }
          },
          "isDeprecated" : false
        }, {
          "name" : "possibleTypes",
          "args" : [ ],
          "type" : {
            "kind" : "LIST",
            "ofType" : {
              "kind" : "NON_NULL",
              "ofType" : {
                "kind" : "OBJECT",
                "name" : "__Type"
              }
            }
          },
          "isDeprecated" : false
        }, {
          "name" : "enumValues",
          "args" : [ {
            "name" : "includeDeprecated",
            "type" : {
              "kind" : "SCALAR",
              "name" : "Boolean"
            },
            "defaultValue" : "false"
          } ],
          "type" : {
            "kind" : "LIST",
            "ofType" : {
              "kind" : "NON_NULL",
              "ofType" : {
                "kind" : "OBJECT",
                "name" : "__EnumValue"
              }
            }
          },
          "isDeprecated" : false
        }, {
          "name" : "inputFields",
          "args" : [ {
            "name" : "includeDeprecated",
            "type" : {
              "kind" : "SCALAR",
              "name" : "Boolean"
            },
            "defaultValue" : "false"
          } ],
          "type" : {
            "kind" : "LIST",
            "ofType" : {
              "kind" : "NON_NULL",
              "ofType" : {
                "kind" : "OBJECT",
                "name" : "__InputValue"
              }
            }
          },
          "isDeprecated" : false
        }, {
          "name" : "ofType",
          "args" : [ ],
          "type" : {
            "kind" : "OBJECT",
            "name" : "__Type"
          },
          "isDeprecated" : false
        }, {
          "name" : "specifiedByUrl",
          "args" : [ ],
          "type" : {
            "kind" : "SCALAR",
            "name" : "String"
          },
          "isDeprecated" : false
        } ],
        "interfaces" : [ ]
      }, {
        "kind" : "ENUM",
        "name" : "__TypeKind",
        "description" : "An enum describing what kind of type a given __Type is",
        "enumValues" : [ {
          "name" : "SCALAR",
          "description" : "Indicates this type is a scalar. 'specifiedByUrl' is a valid field",
          "isDeprecated" : false
        }, {
          "name" : "OBJECT",
          "description" : "Indicates this type is an object. \`fields\` and \`interfaces\` are valid fields.",
          "isDeprecated" : false
        }, {
          "name" : "INTERFACE",
          "description" : "Indicates this type is an interface. \`fields\` and \`possibleTypes\` are valid fields.",
          "isDeprecated" : false
        }, {
          "name" : "UNION",
          "description" : "Indicates this type is a union. \`possibleTypes\` is a valid field.",
          "isDeprecated" : false
        }, {
          "name" : "ENUM",
          "description" : "Indicates this type is an enum. \`enumValues\` is a valid field.",
          "isDeprecated" : false
        }, {
          "name" : "INPUT_OBJECT",
          "description" : "Indicates this type is an input object. \`inputFields\` is a valid field.",
          "isDeprecated" : false
        }, {
          "name" : "LIST",
          "description" : "Indicates this type is a list. \`ofType\` is a valid field.",
          "isDeprecated" : false
        }, {
          "name" : "NON_NULL",
          "description" : "Indicates this type is a non-null. \`ofType\` is a valid field.",
          "isDeprecated" : false
        } ]
      } ],
      "directives" : [ {
        "name" : "include",
        "description" : "Directs the executor to include this field or fragment only when the \`if\` argument is true",
        "locations" : [ "FIELD", "FRAGMENT_SPREAD", "INLINE_FRAGMENT" ],
        "args" : [ {
          "name" : "if",
          "description" : "Included when true.",
          "type" : {
            "kind" : "NON_NULL",
            "ofType" : {
              "kind" : "SCALAR",
              "name" : "Boolean"
            }
          }
        } ]
      }, {
        "name" : "skip",
        "description" : "Directs the executor to skip this field or fragment when the \`if\`'argument is true.",
        "locations" : [ "FIELD", "FRAGMENT_SPREAD", "INLINE_FRAGMENT" ],
        "args" : [ {
          "name" : "if",
          "description" : "Skipped when true.",
          "type" : {
            "kind" : "NON_NULL",
            "ofType" : {
              "kind" : "SCALAR",
              "name" : "Boolean"
            }
          }
        } ]
      }, {
        "name" : "deprecated",
        "description" : "Marks the field, argument, input field or enum value as deprecated",
        "locations" : [ "FIELD_DEFINITION", "ARGUMENT_DEFINITION", "ENUM_VALUE", "INPUT_FIELD_DEFINITION" ],
        "args" : [ {
          "name" : "reason",
          "description" : "The reason for the deprecation",
          "type" : {
            "kind" : "SCALAR",
            "name" : "String"
          },
          "defaultValue" : "\\"No longer supported\\""
        } ]
      }, {
        "name" : "include",
        "description" : "Directs the executor to include this field or fragment only when the \`if\` argument is true",
        "locations" : [ "FIELD", "FRAGMENT_SPREAD", "INLINE_FRAGMENT" ],
        "args" : [ {
          "name" : "if",
          "description" : "Included when true.",
          "type" : {
            "kind" : "NON_NULL",
            "ofType" : {
              "kind" : "SCALAR",
              "name" : "Boolean"
            }
          }
        } ]
      }, {
        "name" : "skip",
        "description" : "Directs the executor to skip this field or fragment when the \`if\`'argument is true.",
        "locations" : [ "FIELD", "FRAGMENT_SPREAD", "INLINE_FRAGMENT" ],
        "args" : [ {
          "name" : "if",
          "description" : "Skipped when true.",
          "type" : {
            "kind" : "NON_NULL",
            "ofType" : {
              "kind" : "SCALAR",
              "name" : "Boolean"
            }
          }
        } ]
      }, {
        "name" : "specifiedBy",
        "description" : "Exposes a URL that specifies the behaviour of this scalar.",
        "locations" : [ "SCALAR" ],
        "args" : [ {
          "name" : "url",
          "description" : "The URL that specifies the behaviour of this scalar.",
          "type" : {
            "kind" : "NON_NULL",
            "ofType" : {
              "kind" : "SCALAR",
              "name" : "String"
            }
          }
        } ]
      } ]
    }
  }
}`);
      console.log(schema1)
      console.log(schema2)
      const windowAny = window as any
      windowAny.GraphQLVoyager.init(this.$el, {
        introspection: schema2,
      });
    },
  },
})
</script>
