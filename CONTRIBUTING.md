# Contributing

The shared protocol targets Java 17, while the current Fabric Loom build requires a Java 21
build JVM. Run the complete verification before opening a pull request:

```bash
./gradlew build
```

Keep protocol changes backward-compatible unless a coordinated Selective Render client update is
included. Test Paper changes on Paper 1.20.1 with PlotSquared 7.3.9 and Fabric changes with the
ArdaCraft PlotSquared 7.3.9-SNAPSHOT stack. Do not bundle PlotSquared or its GPL-licensed code.

Selective Render 1.9.0 still uses bridge protocol version 2. Client-only features do not require an
SRP version bump. Verify both permitted and denied requests when changing permission handling.
Contact: [pengwing.ac@gmail.com](mailto:pengwing.ac@gmail.com).
