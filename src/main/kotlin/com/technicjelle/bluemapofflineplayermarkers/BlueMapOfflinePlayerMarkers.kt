package com.technicjelle.bluemapofflineplayermarkers

import com.technicjelle.BMUtils
import de.bluecolored.bluemap.api.BlueMapAPI
import net.fabricmc.api.DedicatedServerModInitializer
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents
import net.minecraft.server.MinecraftServer
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import java.util.function.Consumer

object BlueMapOfflinePlayerMarkers : DedicatedServerModInitializer {

    const val MOD_ID = "bluemapofflineplayermarkers"

    val logger: Logger = LogManager.getLogger()

    private val markerHandler = MarkerHandler()

    override fun onInitializeServer() {

        ConfigManager

        ServerLifecycleEvents.SERVER_STARTED.register(ServerLifecycleEvents.ServerStarted {
            BlueMapAPI.onEnable(getOnEnableListener(it))
            BlueMapAPI.onDisable(onDisableListener)
        })

        ServerPlayConnectionEvents.JOIN.register(ServerPlayConnectionEvents.Join { handler, _, _ ->
            Thread { markerHandler.remove(handler.player) }.start()
        })

        ServerPlayConnectionEvents.DISCONNECT.register(ServerPlayConnectionEvents.Disconnect { handler, server ->
            handler.player.writePlayerNbt()

            Thread { markerHandler.add(server, handler.player.toOfflinePlayer()) }.start()
        })

        ServerLifecycleEvents.SERVER_STOPPING.register(ServerLifecycleEvents.ServerStopping {
            BlueMapAPI.unregisterListener(getOnEnableListener(it))
            BlueMapAPI.unregisterListener(onDisableListener)
            logger.info("BlueMap Offline Player Markers plugin disabled!")
        })
    }

    private fun getOnEnableListener(server: MinecraftServer) = Consumer<BlueMapAPI> { api ->
        logger.info("API Ready! BlueMap Offline Player Markers plugin enabled!")

        runCatching {
            BMUtils.copyJarResourceToBlueMap(
                api, javaClass.classLoader, "assets/technicjelle/style.css", "bmopm.css", false
            )
            BMUtils.copyJarResourceToBlueMap(
                api, javaClass.classLoader, "assets/technicjelle/script.js", "bmopm.js", false
            )
        }.onFailure { logger.trace("Failed to copy resources to BlueMap webapp!", it) }

        Thread { markerHandler.loadOfflineMarkers(server) }.start()
    }

    private val onDisableListener = Consumer<BlueMapAPI> {
        logger.info("API disabled! BlueMap Offline Player Markers shutting down...")
    }
}