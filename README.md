![logo](./docs/img/koin_main_logo.png)

[![Kotlin](https://img.shields.io/badge/Kotlin-1.9.24-blue.svg?style=flat&logo=kotlin)](https://kotlinlang.org)
![Github Actions](https://github.com/InsertKoinIO/koin/actions/workflows/build.yml/badge.svg)
[![Apache 2 License](https://img.shields.io/github/license/InsertKoinIO/koin)](https://github.com/InsertKoinIO/koin/blob/main/LICENSE.txt)
[![Slack channel](https://img.shields.io/badge/Chat-Slack-orange.svg?style=flat&logo=slack)](https://kotlinlang.slack.com/messages/koin/)


# What is KOIN?
 
Koin is a pragmatic lightweight dependency injection framework for Kotlin developers, developed by [Kotzilla](https://kotzilla.io) and open-source [contributors](https://github.com/InsertKoinIO/koin/graphs/contributors).

`Koin is a DSL, a light container and a pragmatic API`


## Setup & Current Version

Here are the current available Koin project versions: ![](https://img.shields.io/badge/stable-version-blue) ![](https://img.shields.io/badge/unstable-version-orange)

- Koin ![](https://img.shields.io/badge/3.5.6-blue) ![](https://img.shields.io/badge/3.6.0-Beta4-orange)
- Koin for Compose ![](https://img.shields.io/badge/1.1.5-blue) ![](https://img.shields.io/badge/1.2.0-Beta4-orange)
- Koin Annotations ![](https://img.shields.io/badge/1.3.1-blue) ![](https://img.shields.io/badge/1.4.0-Alpha1-orange)

## Koin Packages

| Project   |      Versions     |
|----------|:-------------:|
| [koin-bom](https://mvnrepository.com/artifact/io.insert-koin/koin-bom) |  ![](https://img.shields.io/badge/3.5.6-blue) - ![](https://img.shields.io/badge/3.6.0-Beta4-orange) |
| [koin-core](https://mvnrepository.com/artifact/io.insert-koin/koin-core) |  ![](https://img.shields.io/badge/3.5.6-blue) - ![](https://img.shields.io/badge/3.6.0-Beta4-orange) |
| [koin-core-coroutines](https://mvnrepository.com/artifact/io.insert-koin/koin-core-coroutines) |  ![](https://img.shields.io/badge/3.5.6-blue) - ![](https://img.shields.io/badge/3.6.0-Beta4-orange) |
| [koin-test](https://mvnrepository.com/artifact/io.insert-koin/koin-test) |  ![](https://img.shields.io/badge/3.5.6-blue) - ![](https://img.shields.io/badge/3.6.0-Beta4-orange) |
| [koin-android](https://mvnrepository.com/artifact/io.insert-koin/koin-android) |  ![](https://img.shields.io/badge/3.5.6-blue) - ![](https://img.shields.io/badge/3.6.0-Beta4-orange) |
| [koin-android-test](https://mvnrepository.com/artifact/io.insert-koin/koin-android-test) |  ![](https://img.shields.io/badge/3.5.6-blue) - ![](https://img.shields.io/badge/3.6.0-Beta4-orange) |
| [koin-android-compat](https://mvnrepository.com/artifact/io.insert-koin/koin-android-compat) |  ![](https://img.shields.io/badge/3.5.6-blue) - ![](https://img.shields.io/badge/3.6.0-Beta4-orange) |
| [koin-android-navigation](https://mvnrepository.com/artifact/io.insert-koin/koin-android-navigation) |  ![](https://img.shields.io/badge/3.5.6-blue) - ![](https://img.shields.io/badge/3.6.0-Beta4-orange) |
| [koin-android-workmanager](https://mvnrepository.com/artifact/io.insert-koin/koin-android-workmanager) |  ![](https://img.shields.io/badge/3.5.6-blue) - ![](https://img.shields.io/badge/3.6.0-Beta4-orange) |
| [koin-android-compose](https://mvnrepository.com/artifact/io.insert-koin/koin-android-compose) |  ![](https://img.shields.io/badge/3.5.6-blue) - ![](https://img.shields.io/badge/3.6.0-Beta4-orange) |
| [koin-android-compose-navigation](https://mvnrepository.com/artifact/io.insert-koin/koin-android-compose-navigation) |  ![](https://img.shields.io/badge/3.5.6-blue) - ![](https://img.shields.io/badge/3.6.0-Beta4-orange) |
| [koin-ktor](https://mvnrepository.com/artifact/io.insert-koin/koin-ktor) |  ![](https://img.shields.io/badge/3.5.6-blue) - ![](https://img.shields.io/badge/3.6.0-Beta4-orange) |
| [koin-compose](https://mvnrepository.com/artifact/io.insert-koin/koin-compose) |  ![](https://img.shields.io/badge/1.1.5-blue) - ![](https://img.shields.io/badge/1.2.0-Beta4-orange) |
| [koin-compose-viewmodel](https://mvnrepository.com/artifact/io.insert-koin/koin-android-compose-navigation) |  ![](https://img.shields.io/badge/1.2.0-Beta4-orange) |
| [koin-ktor](https://mvnrepository.com/artifact/io.insert-koin/koin-ktor) |  ![](https://img.shields.io/badge/3.5.6-blue) - ![](https://img.shields.io/badge/3.6.0-Beta4-orange) |
| [koin-logger-slf4](https://mvnrepository.com/artifact/io.insert-koin/koin-logger-slf4) |  ![](https://img.shields.io/badge/3.5.6-blue) - ![](https://img.shields.io/badge/3.6.0-Beta4-orange) |
| [koin-annotations](https://mvnrepository.com/artifact/io.insert-koin/koin-annotations) |  ![](https://img.shields.io/badge/1.3.1-blue) - ![](https://img.shields.io/badge/1.4.0-alpha1-orange) |

🔎 Check the [latest changes](https://github.com/InsertKoinIO/koin/blob/main/CHANGELOG.md) to update your Koin project.

🛠 Follow the [setup page](https://insert-koin.io/docs/setup/koin) for more details


## Get started with Koin Tutorials 🚀

You can find here tutorials to help you learn and get started with Koin framework:
- [Kotlin](https://insert-koin.io/docs/quickstart/kotlin)
- [Kotlin with Koin Annotations](https://insert-koin.io/docs/quickstart/kotlin-annotations)
- [Android](https://insert-koin.io/docs/quickstart/android-viewmodel)
- [Android with Koin Annotations](https://insert-koin.io/docs/quickstart/android-annotations)
- [Android Jetpack Compose](https://insert-koin.io/docs/quickstart/android-compose)
- [Kotlin Multiplatform](https://insert-koin.io/docs/quickstart/kmp)
- [Ktor](https://insert-koin.io/docs/quickstart/ktor)

## Latest News & Resources 🌐
- The official Koin website: [insert-koin.io](https://insert-koin.io)
- Twitter: [@insertkoin_io](https://twitter.com/insertkoin_io)
- Medium: [Koin Developers Hub](https://medium.com/koin-developers)
- Kotzilla Blog: [Kotzilla Blog](https://blog.kotzilla.io/)

## Community 💬

- Come talk on slack [#koin](https://kotlinlang.slack.com/?redir=%2Fmessages%2Fkoin) channel
- Post your question on [Stackoverflow](https://stackoverflow.com/questions/tagged/koin)
- Found a bug or a problem? Open an issue on [Github issues](https://github.com/InsertKoinIO/koin/issues)

## Contributing 🛠

Want to help or share a proposal about Koin? problem on a specific feature? 

- Open an issue to explain the issue you want to solve [Open an issue](https://github.com/InsertKoinIO/koin/issues)
- Come talk on slack [#koin-dev](https://kotlinlang.slack.com/?redir=%2Fmessages%2Fkoin-dev) channel
- After discussion to validate your ideas, you can open a PR or even a draft PR if the contribution is a big one [Current PRs](https://github.com/InsertKoinIO/koin/pulls)

Additional readings about basic setup: https://github.com/InsertKoinIO/koin/blob/master/CONTRIBUTING.adoc

### Contributors

Thank you all for your work! ❤️

<a href="https://github.com/InsertKoinIO/koin/graphs/contributors">
  <img src="https://contrib.rocks/image?repo=InsertKoinIO/koin" />
</a>

## OpenCollective - Sponsorship ❤️

Support this project by becoming a sponsor and be displayed on the offcial website. [[Help us and Become a sponsor!](https://opencollective.com/koin#sponsor)]
