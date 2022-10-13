group = "com.github.sirblobman.plugin"
version = "1.0.0-SNAPSHOT"

val spigotVersion = findProperty("spigot.version") ?: ""

val jenkinsBuildNumber = System.getenv("BUILD_NUMBER") ?: "Unknown"
val baseVersion = findProperty("version.base") as String
val betaVersionString = (findProperty("version.beta") ?: "false") as String
val betaVersion = betaVersionString.toBoolean()
val betaVersionPart = if (betaVersion) "Beta-" else ""
val calculatedVersion = "$baseVersion.$betaVersionPart$jenkinsBuildNumber"

plugins {
    id("java")
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

repositories {
    mavenCentral()

    maven {
        name = "spigot-repo"
        url = uri("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    }

    maven {
        name = "oss-sonatype-snapshots"
        url = uri("https://oss.sonatype.org/content/repositories/snapshots")
    }

    maven {
        name = "sirblobman-public"
        url = uri("https://nexus.sirblobman.xyz/repository/public/")
    }
}

dependencies {
    compileOnly("org.jetbrains:annotations:23.0.0")
    compileOnly("org.spigotmc:spigot-api:$spigotVersion")
    compileOnly("com.github.sirblobman.api:core:2.6-SNAPSHOT")
}

tasks {
    processResources {
        filesMatching("plugin.yml") {
            val bukkitPluginName = findProperty("bukkit.plugin.name") ?: ""
            val bukkitPluginPrefix = findProperty("bukkit.plugin.prefix") ?: ""
            val bukkitPluginDescription = findProperty("bukkit.plugin.description") ?: ""
            val bukkitPluginWebsite = findProperty("bukkit.plugin.website") ?: ""
            val bukkitPluginMain = findProperty("bukkit.plugin.main") ?: ""

            filter<org.apache.tools.ant.filters.ReplaceTokens>("tokens" to mapOf(
                "bukkit.plugin.name" to bukkitPluginName,
                "bukkit.plugin.prefix" to bukkitPluginPrefix,
                "bukkit.plugin.description" to bukkitPluginDescription,
                "bukkit.plugin.website" to bukkitPluginWebsite,
                "bukkit.plugin.main" to bukkitPluginMain,
                "bukkit.plugin.version" to calculatedVersion,
            ))
        }
    }
}
