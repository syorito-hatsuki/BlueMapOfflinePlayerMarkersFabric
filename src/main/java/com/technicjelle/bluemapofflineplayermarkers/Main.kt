package com.technicjelle.bluemapofflineplayermarkers;

import de.bluecolored.bluemap.api.BlueMapAPI;
import dev.syoritohatsuki.duckyupdater.DuckyUpdater;
import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.server.MinecraftServer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

object Main : DedicatedServerModInitializer {

    val logger: Logger = Logger.getGlobal();
    private lateinit var server: MinecraftServer;

    override fun onInitializeServer() {
        ServerLifecycleEvents.SERVER_STARTED.register(ServerLifecycleEvents.ServerStarted { server ->
            this.server = server;
            DuckyUpdater.checkForUpdate("mWxGwd3F", "bluemapofflineplayermarkers");
            BlueMapAPI.onEnable(onEnableListener);
            BlueMapAPI.onDisable(onDisableListener);
        })

        ServerPlayConnectionEvents.JOIN.register(ServerPlayConnectionEvents.Join { handler, _, _ ->
            Thread { MarkerHandler.remove(handler.player) }
        })

        ServerPlayConnectionEvents.DISCONNECT.register(ServerPlayConnectionEvents.Disconnect { handler, _ ->
            Thread { MarkerHandler.add(handler.player) }
        })

        ServerLifecycleEvents.SERVER_STOPPING.register(ServerLifecycleEvents.ServerStopping {
            BlueMapAPI.unregisterListener(onEnableListener);
            BlueMapAPI.unregisterListener(onDisableListener);
            logger.info("BlueMap Offline Player Markers plugin disabled!");
        })
    }

    private val onEnableListener: Consumer<BlueMapAPI> = Consumer { api ->
        logger.info("API Ready! BlueMap Offline Player Markers plugin enabled!");

        // "registerStyle" has to be invoked inside the consumer (=> not in the async scheduled task below)
        runCatching {
            copyResourceToBlueMapWebApp(api, "assets/technicjelle/style.css", "bmopm.css")
            copyResourceToBlueMapWebApp(api, "assets/technicjelle/script.js", "bmopm.js")
        }.onFailure {
            logger.log(Level.SEVERE, "Failed to copy resources to BlueMap webapp!", it);
        }

        Thread { MarkerHandler.loadOfflineMarkers() }
    }

    private val onDisableListener: Consumer<BlueMapAPI> = Consumer {
        logger.info("API disabled! BlueMap Offline Player Markers shutting down...")
        //not much to do here, actually...
    }

    private fun copyResourceToBlueMapWebApp(api: BlueMapAPI, fromResource: String, toAsset: String) {
        val toPath: Path = api.webApp.webRoot.resolve("assets").resolve(toAsset);
        Files.createDirectories(toPath.parent)
        runCatching {
            val input: InputStream? = javaClass.classLoader.getResourceAsStream(fromResource)
            val out: OutputStream =
                Files.newOutputStream(toPath, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)

            if (input == null) throw IOException("Resource not found: $fromResource")
            input.transferTo(out)
        }

        val assetPath = "assets/$toAsset";
        if (toAsset.endsWith(".js")) api.webApp.registerScript(assetPath)
        if (toAsset.endsWith(".css")) api.webApp.registerStyle(assetPath)
    }

}