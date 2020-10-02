# Insulator

![Release](https://github.com/andrea-vinci/Insulator/workflows/Release/badge.svg)
![CI](https://github.com/andrea-vinci/Insulator/workflows/CI/badge.svg)
[![codecov](https://codecov.io/gh/andrea-vinci/Insulator/branch/master/graph/badge.svg)](https://codecov.io/gh/andrea-vinci/Insulator)
[![Known Vulnerabilities](https://snyk.io/test/github/andrea-vinci/Insulator/badge.svg?targetFile=build.gradle)](https://snyk.io/test/github/andrea-vinci/Insulator?targetFile=build.gradle)
[![ktlint](https://img.shields.io/badge/code%20style-%E2%9D%A4-FF4081.svg)](https://ktlint.github.io/)
[![Dependabot Status](https://api.dependabot.com/badges/status?host=github&repo=andrea-vinci/Insulator)](https://dependabot.com)
[![Codacy Badge](https://app.codacy.com/project/badge/Grade/a0c4f55b8cb641bd8c991b6a98d12e1b)](https://www.codacy.com/manual/darka91/Insulator/dashboard?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=darka91/Insulator&amp;utm_campaign=Badge_Grade)

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

