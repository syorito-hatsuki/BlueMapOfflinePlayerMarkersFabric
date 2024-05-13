package com.technicjelle.bluemapofflineplayermarkers.impl.fabric.mixin;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayerEntity.class)
public class ServerPlayerEntityMixin {

    @Unique
    Long lastPlayed = null;

    @Inject(method = "onDisconnect", at = @At("TAIL"))
    private void getDisconnectTime(CallbackInfo ci) {
        lastPlayed = System.currentTimeMillis();
    }

    @Inject(method = "readCustomDataFromNbt", at = @At(value = "TAIL"))
    private void readBukkitNbt(NbtCompound nbt, CallbackInfo ci) {
        lastPlayed = nbt.getLong("lastPlayed");
    }

    @Inject(method = "writeCustomDataToNbt", at = @At(value = "TAIL"))
    private void writeBukkitNbt(NbtCompound nbt, CallbackInfo ci) {
        if (lastPlayed != null) {
            var bukkitNbt = new NbtCompound();
            bukkitNbt.putLong("lastPlayed", lastPlayed);
            nbt.put("bukkit", bukkitNbt);
        }
    }
}
