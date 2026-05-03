package dev.syoritohatsuki.bluemapofflineplayermarkers.impl.fabric.codec;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.Optional;

public record BukkitCodec(Optional<String> lastKnownName, Optional<Long> lastPlayed) {
    public static final Codec<BukkitCodec> CODEC = RecordCodecBuilder.create(
            (instance) -> instance.group(
                    Codec.STRING.optionalFieldOf("lastKnownName")
                            .forGetter((codec) -> codec.lastKnownName),
                    Codec.LONG
                            .optionalFieldOf("lastPlayed")
                            .forGetter((codec) -> codec.lastPlayed)
            ).apply(instance, BukkitCodec::new)
    );
}
