# Contributing

`./gradlew build` compiles and tests every variant: the shared protocol (`common`, Java 17), Paper for Minecraft 1.20.1 and 1.21.1 (`paper/src`, built twice), and Fabric for Minecraft 1.20.1 (`fabric`). Run Gradle with Java 21 or newer.

Keep protocol changes backward-compatible unless a coordinated Selective Render client update is included. Test Paper changes on Paper with PlotSquared 7.3.9 and Fabric changes with the ArdaCraft PlotSquared 7.3.9-SNAPSHOT stack. Do not bundle PlotSquared or its GPL-licensed code. Client-only Selective Render features do not require an SRP version bump. Verify both permitted and denied requests when changing permission handling.

Pushing to `main` builds every variant. If `addon_version` in `gradle.properties` has no tag yet, the workflow publishes a GitHub prerelease with all three JARs, so raising the version on `main` is the release trigger.

Contact: [pengwing.ac@gmail.com](mailto:pengwing.ac@gmail.com).
