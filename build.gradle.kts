import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar


plugins {
    java
    id("com.github.johnrengelman.shadow") version "8.1.1"
    id("xyz.jpenilla.run-paper") version "2.2.2"
}

group = "xyz.zeppelin"
version = "1.1.1"

repositories {
    mavenCentral()
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/") // Spigot
    maven("https://oss.sonatype.org/content/groups/public/")  // Sonatype
    maven("https://s01.oss.sonatype.org/content/repositories/snapshots/") // Sonatype Snapshots
    maven("https://jitpack.io") // Jitpack
    maven("https://repo.demeng.dev/releases") // Sentinel licensing
}

dependencies {
    // Spigot
    compileOnly("org.spigotmc", "spigot-api", "1.16.4-R0.1-SNAPSHOT")
    compileOnly("com.github.MilkBowl", "VaultAPI", "1.7")
    // CommandAPI
    implementation("dev.jorel", "commandapi-bukkit-shade", "9.3.0")
    // Java
    compileOnly("org.projectlombok", "lombok", "1.18.30")
    annotationProcessor("org.projectlombok", "lombok", "1.18.30")
    // Bstats
    implementation("org.bstats", "bstats-bukkit", "3.0.2")

    // Sentinel Licensing
    implementation ("dev.demeng:sentinel-java-wrapper:1.2.0") {
        // Exclude GSON if it's already available
        exclude(group = "com.google.code.gson", module = "gson")
    }
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

tasks {
    processResources {
        expand("version" to version)
    }

    runServer {
        minecraftVersion("1.20.4")
        environment("xyz.zeppelin.development", "true")
    }

    shadowJar {
        relocate("org.bstats", "xyz.zeppelin.casino.bstats")
    }

    create<ShadowJar>("productionJar") {
        group = "build"

        from(sourceSets.main.get().runtimeClasspath)
        archiveFileName = "production-${version}.jar"

        // Relocate some dependencies to avoid conflicts with other plugins
        relocate("dev.jorel.commandapi", "xyz.zeppelin.casino.commandapi")
        relocate("org.bstats", "xyz.zeppelin.casino.bstats")
    }
}
