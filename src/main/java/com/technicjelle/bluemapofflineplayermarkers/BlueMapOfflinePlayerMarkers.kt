package com.technicjelle.bluemapofflineplayermarkers

import de.bluecolored.bluemap.api.BlueMapAPI
import dev.syoritohatsuki.duckyupdater.DuckyUpdater
import net.fabricmc.api.DedicatedServerModInitializer
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents
import net.minecraft.server.MinecraftServer
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardOpenOption
import java.util.function.Consumer

object BlueMapOfflinePlayerMarkers : DedicatedServerModInitializer {

    val logger: Logger = LoggerFactory.getLogger(javaClass.simpleName)
    private lateinit var server: MinecraftServer
    const val MOD_ID = "bluemapofflineplayermarkers"

    override fun onInitializeServer() {

        ConfigManager

        ServerLifecycleEvents.SERVER_STARTED.register(ServerLifecycleEvents.ServerStarted { server ->
            this.server = server
            DuckyUpdater.checkForUpdate("mWxGwd3F", MOD_ID)
            BlueMapAPI.onEnable(onEnableListener)
            BlueMapAPI.onDisable(onDisableListener)
        })

        ServerPlayConnectionEvents.JOIN.register(ServerPlayConnectionEvents.Join { handler, _, _ ->
            Thread { MarkerHandler.remove(handler.player) }.start()
        })

        ServerPlayConnectionEvents.DISCONNECT.register(ServerPlayConnectionEvents.Disconnect { handler, _ ->
            handler.player.writePlayerNbt()

            Thread { MarkerHandler.add(server, handler.player.toOfflinePlayer()) }.start()
        })

        ServerLifecycleEvents.SERVER_STOPPING.register(ServerLifecycleEvents.ServerStopping {
            BlueMapAPI.unregisterListener(onEnableListener)
            BlueMapAPI.unregisterListener(onDisableListener)
            logger.info("BlueMap Offline Player Markers plugin disabled!")
        })
    }

    private val onEnableListener = Consumer<BlueMapAPI> { api ->
        logger.info("API Ready! BlueMap Offline Player Markers plugin enabled!")

        // "registerStyle" has to be invoked inside the consumer (=> not in the async scheduled task below)
        runCatching {
            copyResourceToBlueMapWebApp(api, "assets/technicjelle/style.css", "bmopm.css")
            copyResourceToBlueMapWebApp(api, "assets/technicjelle/script.js", "bmopm.js")
        }.onFailure {
            logger.trace("Failed to copy resources to BlueMap webapp!", it)
        }

        Thread { MarkerHandler.loadOfflineMarkers(server) }.start()
    }

    private val onDisableListener = Consumer<BlueMapAPI> {
        logger.info("API disabled! BlueMap Offline Player Markers shutting down...")
        //not much to do here, actually...
    }

    private fun copyResourceToBlueMapWebApp(api: BlueMapAPI, fromResource: String, toAsset: String) {
        val toPath: Path = api.webApp.webRoot.resolve("assets").resolve(toAsset)
        Files.createDirectories(toPath.parent)
        runCatching {
            val input = javaClass.classLoader.getResourceAsStream(fromResource)
            val out = Files.newOutputStream(toPath, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)

            if (input == null) throw IOException("Resource not found: $fromResource")
            input.transferTo(out)
        }

        val assetPath = "assets/$toAsset"
        if (toAsset.endsWith(".js")) api.webApp.registerScript(assetPath)
        if (toAsset.endsWith(".css")) api.webApp.registerStyle(assetPath)
    }

}