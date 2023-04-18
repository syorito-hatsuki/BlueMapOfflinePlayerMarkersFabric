package com.technicjelle.bluemapofflineplayermarkers

import net.minecraft.nbt.NbtCompound
import net.minecraft.nbt.NbtDouble
import net.minecraft.nbt.NbtIo
import net.minecraft.nbt.NbtList
import net.minecraft.server.MinecraftServer
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.WorldSavePath
import java.io.File
import java.io.FileInputStream
import java.util.*

fun MinecraftServer.getOfflinePlayers(): List<OfflinePlayer> = mutableListOf<OfflinePlayer>().apply {

    val playersOnServer: List<String> = playerManager.playerList.map { it.uuid.toString() }

    val offlineFiles: List<File> =
        File(getSavePath(WorldSavePath.PLAYERDATA).toFile(), BlueMapOfflinePlayerMarkers.MOD_ID).listFiles()?.filter {
            !playersOnServer.contains(it.name.removeSuffix(".dat"))
        } ?: emptyList()

    if (offlineFiles.isEmpty()) return@apply

    offlineFiles.forEach { file ->
        add(getDataFromFiles(file))
    }
}

fun ServerPlayerEntity.toOfflinePlayer(): OfflinePlayer = getDataFromFiles(
    File(
        server.getSavePath(WorldSavePath.PLAYERDATA).toFile(),
        "${BlueMapOfflinePlayerMarkers.MOD_ID}${File.separator}${uuidAsString}.dat"
    )
)

fun getDataFromFiles(offlineData: File): OfflinePlayer {
    FileInputStream(offlineData).use { stream ->
        NbtIo.readCompressed(stream).let { nbt ->
            return OfflinePlayer(
                UUID.fromString(offlineData.name.removeSuffix(".dat")),
                nbt.getString("username"),
                nbt.getLong("lastOnline"),
                nbt.getList("position", 6).map { tag -> (tag as NbtDouble).doubleValue() },
                nbt.getString("dimension"),
                nbt.getInt("gameMode")
            )
        }
    }
}

fun ServerPlayerEntity.writePlayerNbt() {
    File(server.getSavePath(WorldSavePath.PLAYERDATA).toFile(), BlueMapOfflinePlayerMarkers.MOD_ID).apply {
        if (!exists()) mkdir()
        File(this, "$uuidAsString.dat").apply {
            if (!exists()) createNewFile()
            NbtIo.writeCompressed(NbtCompound().apply {
                putString("username", entityName)
                putLong("lastOnline", System.currentTimeMillis())
                put("position", NbtList().apply {
                    add(NbtDouble.of(pos.x))
                    add(NbtDouble.of(pos.y))
                    add(NbtDouble.of(pos.z))
                })
                putString("dimension", world.dimensionKey.value.toString())
                putInt("gameMode", interactionManager.gameMode.id)
            }, this)
        }
    }
}