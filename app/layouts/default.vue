<template>
  <div>
    <b-navbar toggleable="sm" class="navbar navbar-dark bg-dark">
      <b-navbar-brand to="/" class="text-info"><b>Kronicle</b></b-navbar-brand>
      <b-navbar-toggle target="nav-collapse">
        <b-icon icon="list" aria-hidden="true" /> Menu
      </b-navbar-toggle>
      <b-collapse id="nav-collapse" is-nav>
        <b-navbar-nav>
          <b-nav-item to="/all-environments">Environments</b-nav-item>
          <b-nav-item to="/all-components">Components</b-nav-item>
          <b-nav-item to="/all-components/dependencies">Dependencies</b-nav-item>
          <b-nav-item to="/all-components/openapi-specs">OpenAPI</b-nav-item>
          <b-nav-item to="/all-teams">Teams</b-nav-item>
          <b-nav-item to="/all-areas">Areas</b-nav-item>
          <b-nav-item to="/all-tests">Tests</b-nav-item>
          <b-nav-item :to="{ name: 'all-scanners' }">Scanners</b-nav-item>
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
