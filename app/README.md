# Component Catalog App

## Install Node.js

1. Install `asdf` using Homebrew: https://asdf-vm.com/#/core-manage-asdf
2. Install node.js plugin for `asdf`: `$ asdf plugin add nodejs`
3. Install node.js using `asdf`: `$ asdf install nodejs 12.19.1`

## Build Setup

```bash
# install dependencies
$ yarn install

# serve with hot reload at localhost:3000
$ ../gradlew generateTypeScript && yarn dev

# build for production and launch server
$ yarn build
$ yarn start

# generate static project
$ yarn generate
```

For detailed explanation on how things work, check out [Nuxt.js docs](https://nuxtjs.org).

## Running Jest unit tests in dev

The following command will run Jest in watch mode, automatically re-running relevant tests when you change code: 

```bash
$ ../gradlew generateTypeScript && yarn jest-watch
```

## Running Jest unit tests without watching for changes

The following command will run Jest just once: 

```bash
$ ../gradlew generateTypeScript && yarn test
```

## Running Jest unit tests without watching for changes

The following command is similar the one above, except it will automatically update any 
[Jest snapshots](https://jestjs.io/docs/en/snapshot-testing) that have changed: 

```bash
$ ../gradlew generateTypeScript && yarn test -u
```

Make you check the changes in any updated Jest snapshot files before committing them.  
