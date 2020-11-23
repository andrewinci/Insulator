---
layout: page
title: Development
has_children: false
nav_order: 4
---
# Development

🚧 This page is a WIP 🚧

The JDK version used is the adoptjdk 14.

To run the integration tests in headless mode, run

```bash
export _JAVA_OPTIONS="-Djava.awt.headless=true -Dtestfx.robot=glass -Dtestfx.headless=true -Dprism.order=sw -Dprism.text=t2k  -Dtestfx.setup.timeout=2500 -Dheadless.geometry=1920x1080-64"
./gradlew app:integrationTest
```

To package the app, JPackage is used. The call is wrapped into the `gradle` task `app:packageApp`.
Notice that, to package the app in Ubuntu, `binutils` and `fakeroot` are required.
See https://openjdk.java.net/jeps/343 for more info.

### Build the documentation

The documentation for the github page is available under the `/docs/` folder

```bash
bundle exec jekyll serve
```
