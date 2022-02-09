---
layout: page
title: Home
permalink: /wiki/
nav_order: 1
---
<h1 align="center">
  <br>
  <img src="assets/icon.png" alt="Insulator" width="200">
  <br>
  Insulator
  <br>
</h1>

<h4 align="center">A tool for devs to debug Kafka based applications and services</h4>
<br/>
<p align="center">

<a href="https://github.com/andrewinci/Insulator/releases">
    <img src="https://github.com/andrewinci/Insulator/workflows/Release/badge.svg"
         alt="Release"/>
  </a>

<a href="https://github.com/andrewinci/Insulator/actions?query=workflow%3ACI">
    <img src="https://github.com/andrewinci/Insulator/workflows/CI/badge.svg"
         alt="CI"/>
  </a>

<a href="https://codeclimate.com/github/andrewinci/Insulator/test_coverage">
    <img src="https://api.codeclimate.com/v1/badges/b9b6bbebd21238c333ba/test_coverage" 
        alt="Coverage"/>
    </a>

<a href="https://codeclimate.com/github/andrewinci/Insulator/maintainability">
    <img src="https://api.codeclimate.com/v1/badges/b9b6bbebd21238c333ba/maintainability" 
        alt="code quality"/>
    </a>

<a href="https://snyk.io/test/github/andrewinci/Insulator">
    <img src="https://snyk.io/test/github/andrewinci/Insulator/badge.svg"
         alt="Snyk"/>
  </a>


</p>

<p align="center">
  <a href="https://andrewinci.github.io/Insulator/wiki/">Wiki</a> •
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

Download the binary from the latest release for your OS. Learn more [here](https://andrewinci.github.io/Insulator/wiki/Installation/).

[![Mac release](https://badgen.net/badge/icon/Mac%20Os?label=Download%20Latest%20Release&color=orange)](https://github.com/andrewinci/Insulator/releases/latest/download/insulator-mac.zip)
[![Windows release](https://badgen.net/badge/icon/Windows?label=Download%20Latest%20Release&color=orange)](https://github.com/andrewinci/Insulator/releases/latest/download/insulator-win.zip)
[![Debian release](https://badgen.net/badge/icon/Debian?label=Download%20Latest%20Release&color=orange)](https://github.com/andrewinci/Insulator/releases/latest/download/insulator-debian.zip)

![brew cask install andrewinci/tap/insulator](https://badgen.net/badge/icon/brew%20cask%20install%20andrewinci%2Ftap%2Finsulator?label=🍻%20Brew&color=orange)

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
  * Send tombstones
* 🚧  **Consumer groups** 🚧
  * List consumer groups
  * Show topics, partitions and lags
  * Delete consumer groups
* **Cross platform**
  * Windows, macOS and Linux ready.
* **Dark/Light theme**
* **Auto-update**
* **ReadOnly mode**
