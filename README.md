# Component Catalog

The Component Catalog is a web-based dashboard that displays a list of the components in a technology stack and shows various 
information about those components.


## Component Metadata YAML

The metadata that powers the Component Catalog can be found the `component-metadata.yaml` files in various Git repos. 

There are two template repos for creating repos with component-metadata.yaml files:

| Repo | Notes |
|---|---|
| https://github.com/moneysupermarket/component-metadata-repo-template/ | This is a template for creating a new `component-metadata` repo that contains the YAML that describes area(s) and/or team(s) |
| https://github.com/moneysupermarket/component-metadata-codebase-template/ | This is a template for adding a `component-metadata.yaml` file to a codebase repo, with the YAML file describing the component(s) (e.g. service, database, queue etc.) in that codebase |

The repos above contain README.md files that describe the steps you need to follow.  


## Running the Service

Set these environment variables, either on the terminal or in IntelliJ:

```bash
$ export BITBUCKET_SERVER_HOSTS_0_BASE_URL=https://example.com
$ export BITBUCKET_SERVER_HOSTS_0_USERNAME=user.name
$ export BITBUCKET_SERVER_HOSTS_0_PASSWORD=SOME_ACCESS_TOKEN
$ export GIT_HOSTS_0_HOST=https://example.com
$ export GIT_HOSTS_0_USERNAME=user.name
$ export GIT_HOSTS_0_PASSWORD=SOME_ACCESS_TOKEN
$ export GIT_HOSTS_1_HOST=github.com
$ export GIT_HOSTS_1_USERNAME=user.name
$ export GIT_HOSTS_1_PASSWORD=SOME_ACCESS_TOKEN
```

Run the service:

```bash
$ ./gradlew bootRun
```
