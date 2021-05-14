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

<a href="https://github.com/andrewinci/Insulator/releases">
    <img src="https://github.com/andrewinci/Insulator/workflows/Release/badge.svg"
         alt="Release"/>
  </a>

<a href="https://github.com/andrewinci/Insulator/actions?query=workflow%3ACI">
    <img src="https://github.com/andrewinci/Insulator/workflows/CI/badge.svg"
         alt="CI"/>
  </a>

<a href="https://codeclimate.com/github/andrewinci/Insulator/test_coverage">
    <img src="https://api.codeclimate.com/v1/badges/fd385843d031f1ad99f8/test_coverage" 
        alt="Coverage"/>
    </a>

<a href="https://codeclimate.com/github/andrewinci/Insulator/maintainability">
    <img src="https://api.codeclimate.com/v1/badges/fd385843d031f1ad99f8/maintainability" 
        alt="code quality"/>
    </a>

<a href="https://snyk.io/test/github/andrewinci/Insulator?targetFile=build.gradle">
    <img src="https://snyk.io/test/github/andrewinci/Insulator/badge.svg?targetFile=build.gradle"
         alt="Snyk"/>
  </a>

</p>

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
