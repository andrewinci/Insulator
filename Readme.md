# Insulator

![CI](https://github.com/darka91/insulator/workflows/CI/badge.svg)
[![codecov](https://codecov.io/gh/andrea-vinci/insulator/branch/master/graph/badge.svg?token=70FXB1QXTI)](https://codecov.io/gh/darka91/Insulator)
[![Known Vulnerabilities](https://snyk.io/test/github/darka91/Insulator/badge.svg?targetFile=build.gradle)](https://snyk.io/test/github/darka91/Insulator?targetFile=build.gradle)
![Release](https://github.com/darka91/insulator/workflows/Release/badge.svg)
[![ktlint](https://img.shields.io/badge/code%20style-%E2%9D%A4-FF4081.svg)](https://ktlint.github.io/)
[![Dependabot Status](https://api.dependabot.com/badges/status?host=github&repo=darka91/Insulator)](https://dependabot.com)

Insulator is a tool to help development of kafka based applications.

🚨 **Currently under development, use at your own risk** 🚨

## Features

The latest version supports the following features:

- SSL and SASL authentication
- List topics
- Consumer with Avro deserialization and seek
- List subjects in schema registry
- Show all schema version for a certain subject

## Development

### Linux

The gradle task`packageApp` requires `binutils` and `fakeroot`.

