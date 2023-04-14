import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("fabric-loom")
    kotlin("jvm")
    kotlin("plugin.serialization")
}

base {
    val archivesBaseName: String by project
    archivesName.set(archivesBaseName)
}

val javaVersion = JavaVersion.VERSION_17
val loaderVersion: String by project
val minecraftVersion: String by project

val modVersion: String by project
version = modVersion

val mavenGroup: String by project
group = mavenGroup

repositories {
    maven("https://jitpack.io")
    maven("https://api.modrinth.com/maven")
}

dependencies {
    minecraft("com.mojang", "minecraft", minecraftVersion)

    val yarnMappings: String by project
    mappings("net.fabricmc", "yarn", yarnMappings, null, "v2")

    modImplementation("net.fabricmc", "fabric-loader", loaderVersion)

    val fabricVersion: String by project
    modImplementation("net.fabricmc.fabric-api", "fabric-api", fabricVersion)

    modImplementation("com.github.BlueMap-Minecraft", "BlueMapAPI", "v2.4.0")

    val fabricKotlinVersion: String by project
    modImplementation("net.fabricmc", "fabric-language-kotlin", fabricKotlinVersion)

    include(modImplementation("maven.modrinth", "ducky-updater", "zEiFLS5o"))
}

tasks {
    withType<JavaCompile> {
        options.encoding = "UTF-8"
        sourceCompatibility = javaVersion.toString()
        targetCompatibility = javaVersion.toString()
        options.release.set(javaVersion.toString().toInt())
    }

    withType<KotlinCompile> {
        kotlinOptions {
            jvmTarget = javaVersion.toString()
        }
    }

    jar {
        from("LICENSE") {
            rename {
                "${it}_${base.archivesName}"
            }
        }
    }

    processResources {
        filesMatching("fabric.mod.json") {
            expand(
                mutableMapOf(
                    "version" to project.version,
                    "loaderVersion" to loaderVersion,
                    "javaVersion" to javaVersion.toString()
                )
            )
        }
    }

    java {
        toolchain {
            languageVersion.set(JavaLanguageVersion.of(javaVersion.toString()))
        }
        sourceCompatibility = javaVersion
        targetCompatibility = javaVersion
        withSourcesJar()
    }
}