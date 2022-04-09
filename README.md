# Kronicle

Kronicle is an open source tool and dashboard for development teams and tech organizations to document and visualise
the components in their tech stack and teams in their organization.

See the [documentation](https://kronicle.tech) for more information on deploying and using Kronicle.


## Component Metadata YAML

The metadata that powers Kronicle can be found the `kronicle.yaml` files in various Git repos. 

There are two template repos for creating repos with kronicle.yaml files:

| Repo                                                                  | Notes                                                                                                                                                                         |
|-----------------------------------------------------------------------|-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| https://github.com/kronicle-tech/kronicle-metadata-repo-template/     | This is a template for creating a new `kronicle-metadata` repo that contains the YAML that describes area(s) and/or team(s)                                                   |
| https://github.com/kronicle-tech/kronicle-metadata-codebase-template/ | This is a template for adding a `kronicle.yaml` file to a codebase repo, with the YAML file describing the component(s) (e.g. service, database, queue etc.) in that codebase |

The repos above contain README.md files that describe the steps you need to follow.  


## Running the Service

Set these environment variables, either on the terminal or in IntelliJ:

```bash
$ export PLUGINS_GITHUB_ENABLED=true
$ export PLUGINS_GITHUB_USERS_0_ACCOUNT_NAME=some-user-name
$ export PLUGINS_GITHUB_USERS_0_ACCESS_TOKEN_USERNAME=some-user-name
$ export PLUGINS_GITHUB_USERS_0_ACCESS_TOKEN_VALUE=some-personal-access-token
$ export PLUGINS_GIT_HOSTS_0_HOST=github.com
$ export PLUGINS_GIT_HOSTS_0_USERNAME=some-user-name
$ export PLUGINS_GIT_HOSTS_0_PASSWORD=SOME_ACCESS_TOKEN
```

Run the service:

```bash
$ ./gradlew bootRun
```
