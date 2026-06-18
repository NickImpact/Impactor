import ca.stellardrift.build.configurate.ConfigFormats
import ca.stellardrift.build.configurate.catalog.PolyglotVersionCatalogExtension

pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
    }
}

plugins {
    id("ca.stellardrift.polyglot-version-catalogs") version "6.1.0"
}

extensions.configure<PolyglotVersionCatalogExtension> {
    from(ConfigFormats.YAML, file("../gradle/libs.versions.yml"))
}