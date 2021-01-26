![logo](./docs/img/koin_2.0.jpg)

## What is KOIN?
 
A pragmatic lightweight dependency injection framework for Kotlin developers.

`Koin is a DSL, a light container and a pragmatic API`

## Official Website 👉 [https://insert-koin.io](https://insert-koin.io)

### Latest News 🌐

- Follow us on Twitter for latest news: [@insertkoin_io](https://twitter.com/insertkoin_io)
- Koin developers on Medium: [koin developers hub](https://medium.com/koin-developers)

### Getting Help 🚒

Documentation:
* [Getting Stared](https://start.insert-koin.io/)
* [Documentation References](https://doc.insert-koin.io/)

Any question about Koin usage? 
- Come talk on slack [#koin](https://kotlinlang.slack.com/?redir=%2Fmessages%2Fkoin) channel
- Post your question on [Stackoverflow - #koin tag](https://stackoverflow.com/questions/tagged/koin)

### Reporting issues 🚑

Found a bug or a problem on a specific feature? Open an issue on [Github issues](https://github.com/InsertKoinIO/koin/issues)


### Contributing 🛠

Want to help or share a proposal about Koin? problem on a specific feature? 

- Open an issue to explain the issue you want to solve [Open an issue](https://github.com/InsertKoinIO/koin/issues)
- Come talk on slack [#koin-dev](https://kotlinlang.slack.com/?redir=%2Fmessages%2Fkoin-dev) channel
- After discussion to validate your ideas, you can open a PR or even a draft PR if the contribution is a big one [Current PRs](https://github.com/InsertKoinIO/koin/pulls)

Additional readings about basic setup: https://github.com/InsertKoinIO/koin/blob/master/CONTRIBUTING.adoc


# Setup

## Current Version

```gradle
// latest stable
koin_version = '3.0.1-alpha-1'
```

## Gradle 

### Jcenter 

Check that you have the `jcenter` repository. 

```gradle
// Add Jcenter to your repositories if needed
repositories {
	jcenter()    
}
```

### Dependencies

Pick one of your Koin dependency:

#### Core features

```gradle
// Koin for Kotlin (Multiplatform)
implementation "org.koin:koin-core:$koin_version"
// Koin extended & experimental features
implementation "org.koin:koin-core-ext:$koin_version"
// Koin for Unit tests
testImplementation "org.koin:koin-test:$koin_version"
```

#### Android

```gradle
// Main Android features, Scope, ViewModel
implementation "org.koin:koin-android:$koin_version"
// Koin Android Experimental features
implementation "org.koin:koin-android-ext:$koin_version"
// Work Manager
implementation "org.koin:koin-androidx-workmanager:$koin_version"
// Compose features
implementation "org.koin:koin-androidx-compose:$koin_version"
```

#### Ktor

```gradle
// Koin for Ktor Kotlin
implementation "org.koin:koin-ktor:$koin_version"
// SLF4J Logger
implementation "org.koin:koin-logger-slf4j:$koin_version"
```

## Contributors

This project exists thanks to all the people who contribute. [[Contribute](CONTRIBUTING.adoc)].
<a href="https://github.com/InsertKoinIO/koin/graphs/contributors"><img src="https://opencollective.com/koin/contributors.svg?width=890&button=false" /></a>


## OpenCollective

[![Backers on Open Collective](https://opencollective.com/koin/backers/badge.svg)](#backers)
[![Sponsors on Open Collective](https://opencollective.com/koin/sponsors/badge.svg)](#sponsors) 

### Backers

Thank you to all our backers! 🙏 [[Become a backer](https://opencollective.com/koin#backer)]

<a href="https://opencollective.com/koin#backers" target="_blank"><img src="https://opencollective.com/koin/backers.svg?width=890"></a>


### Sponsors

Support this project by becoming a sponsor. Your logo will show up here with a link to your website. [[Become a sponsor](https://opencollective.com/koin#sponsor)]

<a href="https://opencollective.com/koin/sponsor/0/website" target="_blank"><img src="https://opencollective.com/koin/sponsor/0/avatar.svg"></a>
<a href="https://opencollective.com/koin/sponsor/1/website" target="_blank"><img src="https://opencollective.com/koin/sponsor/1/avatar.svg"></a>
<a href="https://opencollective.com/koin/sponsor/2/website" target="_blank"><img src="https://opencollective.com/koin/sponsor/2/avatar.svg"></a>
<a href="https://opencollective.com/koin/sponsor/3/website" target="_blank"><img src="https://opencollective.com/koin/sponsor/3/avatar.svg"></a>
<a href="https://opencollective.com/koin/sponsor/4/website" target="_blank"><img src="https://opencollective.com/koin/sponsor/4/avatar.svg"></a>
<a href="https://opencollective.com/koin/sponsor/5/website" target="_blank"><img src="https://opencollective.com/koin/sponsor/5/avatar.svg"></a>
<a href="https://opencollective.com/koin/sponsor/6/website" target="_blank"><img src="https://opencollective.com/koin/sponsor/6/avatar.svg"></a>
<a href="https://opencollective.com/koin/sponsor/7/website" target="_blank"><img src="https://opencollective.com/koin/sponsor/7/avatar.svg"></a>
<a href="https://opencollective.com/koin/sponsor/8/website" target="_blank"><img src="https://opencollective.com/koin/sponsor/8/avatar.svg"></a>
<a href="https://opencollective.com/koin/sponsor/9/website" target="_blank"><img src="https://opencollective.com/koin/sponsor/9/avatar.svg"></a>


