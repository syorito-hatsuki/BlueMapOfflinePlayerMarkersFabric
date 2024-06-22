import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("fabric-loom")
    kotlin("jvm")
}

base {
    val archivesBaseName: String by project
    archivesName.set(archivesBaseName)
}

val javaVersion = JavaVersion.VERSION_21
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

    modImplementation("com.github.BlueMap-Minecraft", "BlueMapAPI", "v2.6.1")

    include(implementation(files("libs/BMUtils-v1.1.jar"))!!)

    include(modImplementation("com.github.BlueMap-Minecraft", "BlueNBT", "v2.3.0"))

    include(modImplementation("maven.modrinth", "fstats", "2023.12.3"))

    include(modImplementation("maven.modrinth", "ducky-updater-lib", "2023.10.1"))
}

tasks {
    withType<JavaCompile> {
        options.encoding = "UTF-8"
        sourceCompatibility = javaVersion.toString()
        targetCompatibility = javaVersion.toString()
        options.release.set(javaVersion.toString().toInt())
    }

    withType<KotlinCompile> {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_21)
        }
    }

    jar {
        from("LICENSE")
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