<template>
  <div>
    <b-navbar toggleable="sm" class="navbar navbar-dark bg-dark">
      <b-navbar-brand to="/" class="text-info"><b>Kronicle</b></b-navbar-brand>
      <b-navbar-toggle target="nav-collapse">
        <b-icon
          icon="list"
          aria-hidden="true"
          width="1.171875rem"
          height="1.171875rem"
        />
        Menu
      </b-navbar-toggle>
      <b-collapse id="nav-collapse" is-nav>
        <b-navbar-nav>
          <b-nav-item to="/environments"> Environments </b-nav-item>
          <b-nav-item to="/components"> Components </b-nav-item>
          <b-nav-item to="/diagrams"> Diagrams </b-nav-item>
          <b-nav-item to="/docs"> Docs </b-nav-item>
          <b-nav-item to="/openapi-specs"> OpenAPI </b-nav-item>
          <b-nav-item to="/graphql-schemas"> GraphQL </b-nav-item>
          <b-nav-item to="/teams"> Teams </b-nav-item>
          <b-nav-item to="/areas"> Areas </b-nav-item>
          <b-nav-item to="/tests"> Tests </b-nav-item>
          <b-nav-item to="/scanners"> Scanners </b-nav-item>
        </b-navbar-nav>
      </b-collapse>
    </b-navbar>
    <div class="mx-2 mt-2">
      <Message />
    </div>
    <div id="content">
      <Nuxt />
    </div>
    <Version />
  </div>
</template>

<script lang="ts">
import Vue from 'vue'
import Message from '~/components/Message.vue'
import Version from '~/components/Version.vue'

export default Vue.extend({
  components: {
    Message,
    Version,
  },
})
</script>

<style>
#content a {
  color: #3498db;
  text-decoration: underline;
}

#content a.btn {
  color: white;
  text-decoration: none;
}

#content .table-primary a {
  color: white;
}

#content table {
  margin-bottom: 1rem;
}
</style>
