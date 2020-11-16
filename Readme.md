<h1 align="center">
  <br>
  <img src="assets/icon.png" alt="Insulator" width="200">
  <br>
  Insulator
  <br>
</h1>

<h4 align="center">A tool for devs to debug Kafka based applications and services</h4>

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
<p align="center"><strong> 🚧🚧Currently under development, use at your own risk 🚧🚧 </strong></p>

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

### Mac
```
brew cask install andrea-vinci/insulator/insulator
```

## Key Features

* Authenticate to clusters with: SSL and SASL (SCRAM or PLAIN) authentication
* Topics
    - List available topics
    - Messages count and basic topic info
    - Delete a topic
    - Create a topic with basic configuration (Compaction policy, \#Partitions, ...)
* Consumer
    - Avro and String deserialization
    - Seek based on record timestamp
    - Easy search and filtering
* Schema registry
    - List subjects
    - Show all schema versions for a given subject
* Producer
    - Avro producer with **autocompletion** based on the schema
    - String producer
* Cross platform
    - Windows, macOS and Linux ready.
* Dark/Light theme
* Auto-update

## Development
**🚧Work in progress 🚧**
### Linux

The gradle task `packageApp` requires `binutils` and `fakeroot`.

## Credits

- [JetBrains](https://www.jetbrains.com/?from=Insulator)
- [update4j](https://github.com/update4j/update4j)

## Support

<a href="https://www.buymeacoffee.com/andreavinci" target="_blank"><img src="https://www.buymeacoffee.com/assets/img/custom_images/orange_img.png" alt="Buy Me A Coffee" style="height: 41px !important;width: 174px !important;box-shadow: 0px 3px 2px 0px rgba(190, 190, 190, 0.5) !important;-webkit-box-shadow: 0px 3px 2px 0px rgba(190, 190, 190, 0.5) !important;" ></a>

## License

GPL-3.0
