package net.darkwyvbat.dwbcore.world.entity;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.NotNull;

public enum MobState implements StringRepresentable {
    STANDING("standing", 0),
    CROUCHING("crouching", 1),
    SITTING("sitting", 2),
    SWIMMING("swimming", 3),
    SLEEPING("sleeping", 4);

    public static final Codec<MobState> CODEC = StringRepresentable.fromEnum(MobState::values);
    public static final StreamCodec<ByteBuf, MobState> STREAM_CODEC = ByteBufCodecs.idMapper(ByIdMap.continuous(MobState::getId, values(), ByIdMap.OutOfBoundsStrategy.ZERO), MobState::getId);

    private final String name;
    private final int id;

    MobState(String name, int id) {
        this.name = name;
        this.id = id;
    }

    public int getId() {
        return id;
    }

    @Override
    public @NotNull String getSerializedName() {
        return name;
    }
}
