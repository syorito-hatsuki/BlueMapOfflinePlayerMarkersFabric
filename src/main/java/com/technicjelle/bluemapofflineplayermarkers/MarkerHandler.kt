package com.technicjelle.bluemapofflineplayermarkers

import com.technicjelle.bluemapofflineplayermarkers.Main.logger
import de.bluecolored.bluemap.api.BlueMapAPI
import de.bluecolored.bluemap.api.BlueMapMap
import net.minecraft.server.MinecraftServer
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.WorldSavePath
import net.minecraft.util.math.BlockPos
import net.minecraft.world.GameMode
import java.io.File
import java.io.IOException
import java.util.logging.Level
import javax.imageio.ImageIO

object MarkerHandler {

    /**
     * Adds a player marker to the map.
     *
     * @param player   The player to add the marker for.
     * @param location The location to put the marker at.
     * @param gameMode The game mode of the player.
     */
    fun add(
        player: ServerPlayerEntity,
        location: BlockPos = player.blockPos,
        gameMode: GameMode = player.interactionManager.gameMode
    ) {

    }

    /**
     * For when BlueMap doesn't have an icon for this player yet, so we need to make it create one.
     * @return Whether the player head was created successfully. <br>
     * If <code>true</code>, the player head was created successfully.<br>
     * If <code>false</code>, the player head was not created successfully and the fallback icon should be used instead.
     */
    fun createPlayerHead(player: ServerPlayerEntity, assetName: String, api: BlueMapAPI, map: BlueMapMap): Boolean {
        try {
            val oImgSkin = api.plugin.skinProvider.load(player.uuid)
            if (oImgSkin.isEmpty) {
                logger.log(Level.SEVERE, "${player.name} doesn't have a skin")
                return false // Failure
            }
            logger.info("Saving skin for ${player.name} to $assetName")
            try {
                map.assetStorage.writeAsset(assetName).use { out ->
                    ImageIO.write(api.plugin.playerMarkerIconFactory.apply(player.uuid, oImgSkin.get()), "png", out)
                    return true // Success
                }
            } catch (e: IOException) {
                logger.log(Level.SEVERE, "Failed to write ${player.name}'s head to asset-storage", e)
            }
        } catch (e: IOException) {
            logger.log(Level.SEVERE, "Failed to load skin for player ${player.name}", e)
        }
        return false // Failure
    }

    /**
     * Removes a player marker from the map.
     *
     * @param player The player to remove the marker for.
     */
    fun remove(player: ServerPlayerEntity) {
        val optionalApi = BlueMapAPI.getInstance()
        if (optionalApi.isEmpty) {
            logger.warning("Tried to remove a marker, but BlueMap wasn't loaded!")
            return
        }

        // remove all markers with the players uuid
        optionalApi.get().maps.forEach { map ->
            map.markerSets[ConfigManager.read().markerSetName]?.remove(player.uuidAsString)
        }

        logger.info("Marker for ${player.name} removed")
    }

    /**
     * Load in markers of all offline players by going through the playerdata NBT
     */
    fun loadOfflineMarkers(server: MinecraftServer) {
        //I really don't like "getWorlds().get(0)" as a way to get the main world, but as far as I can tell there is no other way
        val playerDataFolder: File = server.getSavePath(WorldSavePath.PLAYERDATA).toFile()

        //Return if playerdata is missing for some reason.
        if (!playerDataFolder.exists() || !playerDataFolder.isDirectory) return



    }
}