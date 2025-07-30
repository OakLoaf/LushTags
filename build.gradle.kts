plugins {
    `java-library`
    id("com.gradleup.shadow") version("8.3.0")
    id("xyz.jpenilla.run-paper") version("2.2.4")
}

group = "org.lushplugins"
version = "1.0.0-alpha2"

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
    compileOnly("me.clip:placeholderapi:2.11.6")

    // Libraries
    implementation("org.lushplugins:LushLib:0.10.78")
    implementation("io.github.revxrsal:lamp.common:4.0.0-rc.12")
    implementation("io.github.revxrsal:lamp.bukkit:4.0.0-rc.12")
    implementation("org.lushplugins:GuiHandler:1.0.0-alpha25")
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
