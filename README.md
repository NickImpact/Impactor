# Impactor

A developer API meant to streamline work in a common environment, and support any sort of Minecraft environment, modded or vanilla.
Additionally, Impactor attempts to maintain commonality amongst its codebase across Minecraft versions.

Impactor's primary objective is to simplify the work other developers are required to complete, while also
allowing them to better support multiple platforms. Examples include:

- ItemStack Creation
- Players and Entities
- Scoreboards
- Economy

## Provided Feature Set
While Impactor is a developer API at heart, it also provides a set of provided features server owners can 
immediately leverage just with installing this API.

### Economy
A built-in service for the Economy API is provided by default, allowing you to establish a ready to go
economy system. 

The provided system supports multiple types of currencies and provides basic commands capable of interacting
with the system. We also provide a /baltop command for those interested parties.

Should you be using another EconomyService, Impactor's API allows the replacement of our default service
with a service which can mirror any API calls to the service you yourself are using. While this will
require code, Impactor can be leveraged to feed information to other plugins while not requiring plugins
to outright support multiple different economy systems.

## What Platforms Does Impactor Support
As of 5.2.0-SNAPSHOT, Impactor supports the following platforms:
- Fabric
- Forge

Support is planned for the following platforms:
- Paper
- Velocity

## Supported Minecraft Versions
As of 5.2.0, Impactor supports the following Minecraft versions:
- 1.20.1

Support is planned for the following versions:
- 1.20.4
- 1.20.5

## Using Impactor in your Projects
Impactor does its best to provide API access in a manner that allows you to only pull in what you need. With this
in mind, here's an example of pulling in the current economy API:

```kotlin
repositories {
    maven("https://maven.impactdev.net/repository/development")
}

dependencies {
    implementation("net.impactdev.impactor.api:economy:5.2.0-SNAPSHOT")
}
```
