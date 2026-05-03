package com.technicjelle.bluemapofflineplayermarkers.impl.fabric.mixin;

import com.mojang.authlib.GameProfile;
import com.technicjelle.bluemapofflineplayermarkers.impl.fabric.codec.BukkitCodec;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;

/**
 * Compatibility layer with Bukkit-based servers to provide seamless migration.
 *
 * @author Syorito Hatsuki
 *
 */
@Mixin(ServerPlayer.class)
public abstract class ServerPlayerMixin extends Player {

    @Unique
    Long lastPlayed = null;

    public ServerPlayerMixin(Level level, GameProfile gameProfile) {
        super(level, gameProfile);
    }

    @Inject(method = "disconnect", at = @At("TAIL"))
    private void getDisconnectTime(CallbackInfo ci) {
        lastPlayed = System.currentTimeMillis();
    }

    @Inject(method = "readAdditionalSaveData", at = @At(value = "TAIL"))
    private void readBukkitNbt(ValueInput input, CallbackInfo ci) {
        var bukkit = input.read("bukkit", BukkitCodec.CODEC);
        if (bukkit.isEmpty()) return;

        if (bukkit.get().lastPlayed().isPresent()) lastPlayed = bukkit.get().lastPlayed().get();
    }

    @Inject(method = "addAdditionalSaveData", at = @At(value = "TAIL"))
    private void writeBukkitNbt(ValueOutput output, CallbackInfo ci) {
        if (lastPlayed != null) {
            output.store("bukkit", BukkitCodec.CODEC, new BukkitCodec(Optional.of(getPlainTextName()), Optional.of(lastPlayed)));
        }
    }
}
