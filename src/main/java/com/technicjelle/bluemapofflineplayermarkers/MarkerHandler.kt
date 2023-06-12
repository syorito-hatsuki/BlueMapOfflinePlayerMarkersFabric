package com.technicjelle.bluemapofflineplayermarkers

import com.technicjelle.BMUtils
import com.technicjelle.bluemapofflineplayermarkers.BlueMapOfflinePlayerMarkers.logger
import de.bluecolored.bluemap.api.BlueMapAPI
import de.bluecolored.bluemap.api.BlueMapMap
import de.bluecolored.bluemap.api.BlueMapWorld
import de.bluecolored.bluemap.api.markers.MarkerSet
import de.bluecolored.bluemap.api.markers.POIMarker
import net.minecraft.server.MinecraftServer
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.Identifier
import java.io.IOException
import javax.imageio.ImageIO

class MarkerHandler {

    fun add(server: MinecraftServer, player: OfflinePlayer) {
        val optionalApi = BlueMapAPI.getInstance()

        if (optionalApi.isEmpty) {
            logger.warn("Tried to add a marker, but BlueMap wasn't loaded!")
            return
        }

        val api = optionalApi.get()

        if (!api.webApp.getPlayerVisibility(player.uuid)) return

        if (ConfigManager.read().hiddenGameModes.any { it.id == player.gameMode }) return

        val blueMapWorld: BlueMapWorld = api.getWorld(
            server.worlds.find { it.dimensionKey.value == Identifier(player.dimension) }
        ).orElse(null) ?: return

        val markerBuilder = POIMarker.builder()
            .label(player.name)
            .detail(
                player.name + " <i>(offline)</i><br>"
                        + "<bmopm-datetime data-timestamp=" + player.lastTimeOnline + "></bmopm-datetime>"
            )
            .styleClasses("bmopm-offline-player")
            .position(player.position[0], player.position[1] + 1.8, player.position[2])

        blueMapWorld.maps.forEach { map ->
            markerBuilder.icon(BMUtils.getPlayerHeadIconAddress(api, player.uuid, map), 0, 0)

            val markerSet = map.markerSets.computeIfAbsent(ConfigManager.read().markerSetName) {
                MarkerSet.builder().label(ConfigManager.read().markerSetName)
                    .toggleable(ConfigManager.read().toggleable)
                    .defaultHidden(ConfigManager.read().defaultHidden)
                    .build()
            }

            markerSet.put(player.uuid.toString(), markerBuilder.build())

        }

        logger.info("Marker for " + player.uuid + " added")

    }

    private fun createPlayerHead(player: OfflinePlayer, assetName: String, api: BlueMapAPI, map: BlueMapMap): Boolean {
        try {
            val oImgSkin = api.plugin.skinProvider.load(player.uuid)
            if (oImgSkin.isEmpty) {
                logger.warn("${player.name} doesn't have a skin")
                return false // Failure
            }
            logger.info("Saving skin for ${player.name} to $assetName")
            try {
                map.assetStorage.writeAsset(assetName).use { out ->
                    ImageIO.write(api.plugin.playerMarkerIconFactory.apply(player.uuid, oImgSkin.get()), "png", out)
                    return true // Success
                }
            } catch (e: IOException) {
                logger.trace("Failed to write ${player.name}'s head to asset-storage", e)
            }
        } catch (e: IOException) {
            logger.trace("Failed to load skin for player ${player.name}", e)
        }
        return false // Failure
    }

    fun remove(player: ServerPlayerEntity) {
        val optionalApi = BlueMapAPI.getInstance()

        if (optionalApi.isEmpty) {
            logger.warn("Tried to remove a marker, but BlueMap wasn't loaded!")
            return
        }

        optionalApi.get().maps.forEach { map ->
            map.markerSets[ConfigManager.read().markerSetName]?.remove(player.uuidAsString)
        }

        logger.info("Marker for ${player.entityName} removed")
    }

    fun loadOfflineMarkers(server: MinecraftServer) {
        server.getOfflinePlayers().forEach {

            val timeSinceLastPlayed: Long = System.currentTimeMillis() - it.lastTimeOnline

            logger.info("Player " + it.name + " was last seen " + timeSinceLastPlayed + "ms ago")

            if (ConfigManager.read().expireTimeInHours > 0 && timeSinceLastPlayed > ConfigManager.read().expireTimeInHours * 60 * 60 * 1000) {
                logger.info("Player " + it.name + " was last seen too long ago, skipping")
                return@forEach
            }

            add(server, it)

        }
    }
}