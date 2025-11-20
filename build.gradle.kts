import java.io.BufferedReader
import java.io.InputStreamReader

plugins {
    `java-library`
    id("com.gradleup.shadow") version("8.3.0")
    id("xyz.jpenilla.run-paper") version("2.2.4")
    id("com.modrinth.minotaur") version ("2.+")
}

group = "org.lushplugins"
version = "1.0.0-alpha8"

repositories {
    mavenLocal()
    mavenCentral()
    maven("https://oss.sonatype.org/content/groups/public/")
    maven("https://repo.papermc.io/repository/maven-public/") // Paper
    maven("https://repo.lushplugins.org/snapshots/") // LushLib, GuiHandler, PlaceholderHandler
    maven("https://repo.helpch.at/releases/") // PlaceholderAPI
}

dependencies {
    // Dependencies
    compileOnly("io.papermc.paper:paper-api:1.21.7-R0.1-SNAPSHOT")
    compileOnly("com.mysql:mysql-connector-j:9.3.0")
    compileOnly("org.xerial:sqlite-jdbc:3.46.0.0")

    // Soft Dependencies
    compileOnly("me.clip:placeholderapi:2.11.7")

    // Libraries
    implementation("org.lushplugins:LushLib:0.10.83")
    implementation("com.zaxxer:HikariCP:7.0.2")
    implementation("io.github.revxrsal:lamp.common:4.0.0-rc.14")
    implementation("io.github.revxrsal:lamp.bukkit:4.0.0-rc.14")
    implementation("org.lushplugins:GuiHandler:1.0.0-alpha30")
    implementation("org.lushplugins:PlaceholderHandler:1.0.0-alpha6")
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(21))

    registerFeature("optional") {
        usingSourceSet(sourceSets["main"])
    }

    withSourcesJar()
}

tasks {
    withType<JavaCompile> {
        options.encoding = "UTF-8"
        options.compilerArgs.add("-parameters")
    }

    shadowJar {
        relocate("org.lushplugins.lushlib", "org.lushplugins.lushtags.libraries.lushlib")

        minimize()

        archiveFileName.set("${project.name}-${project.version}.jar")
    }

    processResources{
        filesMatching("plugin.yml") {
            expand(project.properties)
        }

        inputs.property("version", rootProject.version)
        filesMatching("plugin.yml") {
            expand("version" to rootProject.version)
        }
    }

    runServer {
        minecraftVersion("1.21.7")

        downloadPlugins {
            hangar("PlaceholderAPI", "2.11.6")
        }
    }
}

modrinth {
    token.set(System.getenv("MODRINTH_TOKEN"))
    projectId.set("jPX54WG8")
    if (System.getenv("RELEASE_TYPE") == "release") {
        versionNumber.set(rootProject.version.toString())
        changelog.set(getChangelogSinceLastTag())
    } else {
        versionNumber.set("${rootProject.version}-${getCurrentCommitHash()}")
    }
    uploadFile.set(file("build/libs/${project.name}-${project.version}.jar"))
    versionType.set(System.getenv("RELEASE_TYPE"))
    gameVersions.addAll(
        "1.18", "1.18.1", "1.18.2",
        "1.19", "1.19.1", "1.19.2", "1.19.3", "1.19.4",
        "1.20", "1.20.1", "1.20.2", "1.20.3", "1.20.4", "1.20.5", "1.20.6",
        "1.21", "1.21.1", "1.21.2", "1.21.3", "1.21.4", "1.21.5", "1.21.6", "1.21.7", "1.21.8", "1.21.9", "1.21.10"
    )
    loaders.addAll("spigot", "paper", "purpur")
    syncBodyFrom.set(rootProject.file("README.md").readText())
}

tasks.modrinth {
    dependsOn("shadowJar")
    dependsOn(tasks.modrinthSyncBody)
}

fun getCurrentCommitHash(): String {
    val process = ProcessBuilder("git", "rev-parse", "--short", "HEAD").start()
    val reader = BufferedReader(InputStreamReader(process.inputStream))
    val commitHash = reader.readLine()
    reader.close()
    process.waitFor()
    if (process.exitValue() == 0) {
        return commitHash ?: ""
    } else {
        throw IllegalStateException("Failed to retrieve the commit hash.")
    }
}

fun getLastTag(): String {
    return ProcessBuilder("git", "describe", "--tags", "--abbrev=0")
        .start().inputStream.bufferedReader().readText().trim()
}

fun getChangelogSinceLastTag(): String {
    return ProcessBuilder("git", "log", "${getLastTag()}..HEAD", "--pretty=format:* %s ([#%h](https://github.com/OakLoaf/${rootProject.name}/commit/%H))")
        .start().inputStream.bufferedReader().readText().trim()
}
