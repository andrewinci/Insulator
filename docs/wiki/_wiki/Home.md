---
layout: page
title: Home
permalink: /wiki/
nav_order: 1
---
<h1 align="center">
  <br>
  <img src="../images/logo.png" alt="Insulator" width="200">
  <br>
  Insulator
  <br>
</h1>

<h4 align="center">A tool for devs to debug Kafka based applications and services</h4>
<br/>
<p align="center">

<a href="https://github.com/andrea-vinci/Insulator/releases">
    <img src="https://github.com/andrea-vinci/Insulator/workflows/Release/badge.svg"
         alt="Release"/>
  </a>

<a href="https://github.com/andrea-vinci/Insulator/actions?query=workflow%3ACI">
    <img src="https://github.com/andrea-vinci/Insulator/workflows/CI/badge.svg"
         alt="CI"/>
  </a>

<a href="https://codeclimate.com/github/andrea-vinci/Insulator/test_coverage">
    <img src="https://api.codeclimate.com/v1/badges/fd385843d031f1ad99f8/test_coverage" 
        alt="Coverage"/>
    </a>

<a href="https://codeclimate.com/github/andrea-vinci/Insulator/maintainability">
    <img src="https://api.codeclimate.com/v1/badges/fd385843d031f1ad99f8/maintainability" 
        alt="code quality"/>
    </a>

<a href="https://snyk.io/test/github/andrea-vinci/Insulator?targetFile=build.gradle">
    <img src="https://snyk.io/test/github/andrea-vinci/Insulator/badge.svg?targetFile=build.gradle"
         alt="Snyk"/>
  </a>

</p>

<p align="center">
  <a href="#key-features">Key Features</a> •
  <a href="#development">Development</a> •
  <a href="#credits">Credits</a> •
  <a href="#support">Support</a> •
  <a href="#license">License</a>
</p>

<p align="center">
        <img src="assets/insulator_dark_mode.gif">
</p>

## Installation

Download the binary from the latest release for your OS.

[![Mac release](https://badgen.net/badge/icon/Mac%20Os?label=Download%20Latest%20Release&color=orange)](https://github.com/andrea-vinci/Insulator/releases/download/0.3.2/insulator-mac.zip)
[![Windows release](https://badgen.net/badge/icon/Windows?label=Download%20Latest%20Release&color=orange)](https://github.com/andrea-vinci/Insulator/releases/download/0.3.2/insulator-win.zip)
[![Debian release](https://badgen.net/badge/icon/Debian?label=Download%20Latest%20Release&color=orange)](https://github.com/andrea-vinci/Insulator/releases/download/0.3.2/insulator-debian.zip)

![brew cask install andrea-vinci/tap/insulator](https://badgen.net/badge/icon/brew%20cask%20install%20andrea-vinci%2Ftap%2Finsulator?label=🍻%20Brew&color=orange)

## Key Features

* **Clusters**
  * Authentication with: PLAN, SSL and SASL (SCRAM or PLAIN) authentication
* **Topics**
  * List available topics
  * Messages count and basic topic info
  * Delete a topic
  * Create a topic with basic configuration (Compaction policy, \#Partitions, ...)
  * Topic configurations
* **Consumer**
  * Avro and String deserialization
  * Seek based on record timestamp
  * Easy search and filtering
* **Schema registry**
  * List subjects
  * Show all schema versions for a given subject
* **Producer**
  * Avro producer with **autocompletion** based on the schema
  * String producer
* 🚧  **Consumer groups** 🚧
  * List consumer groups
* **Cross platform**
  * Windows, macOS and Linux ready.
* **Dark/Light theme**
* **Auto-update**

## Development

The JDK version used is the adoptjdk 14.

To run the integration tests in headless mode, run

```bash
export _JAVA_OPTIONS="-Djava.awt.headless=true -Dtestfx.robot=glass -Dtestfx.headless=true -Dprism.order=sw -Dprism.text=t2k  -Dtestfx.setup.timeout=2500 -Dheadless.geometry=1920x1080-64"
./gradlew app:integrationTest
```

To package the app, JPackage is used. The call is wrapped into the `gradle` task `app:packageApp`.
Notice that, to package the app in Ubuntu, `binutils` and `fakeroot` are required.
See https://openjdk.java.net/jeps/343 for more info.

### Build documentation

The documentation for the github page is available under the `/docs/` folder

```bash
bundle exec jekyll serve
```

## Credits

[JetBrains](https://www.jetbrains.com/?from=Insulator)

[update4j](https://github.com/update4j/update4j)

## Support

<a href="https://www.buymeacoffee.com/andreavinci" target="_blank"><img src="https://www.buymeacoffee.com/assets/img/custom_images/orange_img.png" alt="Buy Me A Coffee" style="height: 41px !important;width: 174px !important;box-shadow: 0px 3px 2px 0px rgba(190, 190, 190, 0.5) !important;-webkit-box-shadow: 0px 3px 2px 0px rgba(190, 190, 190, 0.5) !important;" ></a>

## License

GPL-3.0
