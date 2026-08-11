# Contributing

The shared protocol targets Java 17, while the current Fabric Loom build requires a Java 21
build JVM. Run the complete verification before opening a pull request:

```bash
./gradlew build
```

Keep protocol changes backward-compatible unless a coordinated Selective Render client update is
included. Test Paper changes on Paper 1.20.1 with PlotSquared 7.3.9 and Fabric changes with the
ArdaCraft PlotSquared 7.3.9-SNAPSHOT stack. Do not bundle PlotSquared or its GPL-licensed code.
