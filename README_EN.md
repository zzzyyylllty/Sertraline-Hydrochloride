# Sertraline-Hydrochloride Items

## What's This?


## Build Release Version / Build INGame Plugin
The release version is intended for normal use and does not include the TabooLib ontology.

```
./gradlew build
```

## Build development version
The development version includes the TabooLib ontology for developers to use, but it is not runnable.

```
./gradlew taboolibBuildApi -PDeleteCode
```

>The parameter PDeleteCode indicates the removal of all logical code to reduce volume.
