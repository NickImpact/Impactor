import extensions.writeVersion

plugins {
    id("impactor.loom-conventions")
}

architectury {
    platformSetupLoomIde()
    fabric()
}

dependencies {
    sequenceOf(
        projects.loader,
        projects.api.core,
        projects.api.networking,
        projects.api.storage,
        projects.api.schedulers,
    ).forEach {
        api(it)
        bundle(it)
    }

    modImplementation(libs.fabric.loader)
    sequenceOf("fabric-lifecycle-events-v1", "fabric-networking-api-v1").forEach {
        modImplementation(fabricApi.module(it, rootProject.property("fabric-api").toString()))
    }
}

tasks {

}