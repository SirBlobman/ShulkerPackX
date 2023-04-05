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
    id("maven-publish")
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

repositories {
    mavenCentral()
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    maven("https://oss.sonatype.org/content/repositories/snapshots/")
    maven("https://nexus.sirblobman.xyz/public/")
    maven("https://nexus.sirblobman.xyz/proxy-jitpack/")
}

dependencies {
    compileOnly("org.jetbrains:annotations:24.0.1")
    compileOnly("org.spigotmc:spigot-api:$spigotVersion")
    compileOnly("com.github.sirblobman.api:core:2.8-SNAPSHOT")
    compileOnly("com.github.MilkBowl:VaultAPI:1.7")
}

tasks {
    named<Jar>("jar") {
        archiveFileName.set("ShulkerPackX-$calculatedVersion.jar")
    }

    processResources {
        val pluginName = (findProperty("bukkit.plugin.name") ?: "") as String
        val pluginPrefix = (findProperty("bukkit.plugin.prefix") ?: "") as String
        val pluginDescription = (findProperty("bukkit.plugin.description") ?: "") as String
        val pluginWebsite = (findProperty("bukkit.plugin.website") ?: "") as String
        val pluginMainClass = (findProperty("bukkit.plugin.main") ?: "") as String

        filesMatching("plugin.yml") {
            filter {
                it.replace("\${bukkit.plugin.name}", pluginName)
                    .replace("\${bukkit.plugin.prefix}", pluginPrefix)
                    .replace("\${bukkit.plugin.description}", pluginDescription)
                    .replace("\${bukkit.plugin.website}", pluginWebsite)
                    .replace("\${bukkit.plugin.version}", calculatedVersion)
                    .replace("\${bukkit.plugin.main}", pluginMainClass)
            }
        }
    }
}

publishing {
    repositories {
        maven {
            name = "sirblobman-public"
            url = uri("https://nexus.sirblobman.xyz/repository/public-snapshots/")

            credentials {
                val currentUsername = System.getenv("MAVEN_DEPLOY_USERNAME") ?: findProperty("mavenUsernameSirBlobman") ?: ""
                val currentPassword = System.getenv("MAVEN_DEPLOY_PASSWORD") ?: findProperty("mavenPasswordSirBlobman") ?: ""

                username = currentUsername as String
                password = currentPassword as String
            }
        }
    }

    publications {
        create<MavenPublication>("maven") {
            groupId = "com.github.sirblobman.plugin"
            artifactId = "ShulkerPackX"
            version = "1.0.0-SNAPSHOT"
            artifact(tasks["jar"])
        }
    }
}
