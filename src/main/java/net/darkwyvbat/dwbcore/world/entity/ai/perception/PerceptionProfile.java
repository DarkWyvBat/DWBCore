package net.darkwyvbat.dwbcore.world.entity.ai.perception;

import com.mojang.serialization.Codec;
import net.minecraft.util.Mth;

//TODO
public class PerceptionProfile {
    private int dangerLevel;

    public static final Codec<PerceptionProfile> CODEC = Codec.unit(new PerceptionProfile());

    public int getDangerLevel() {
        return dangerLevel;
    }

    public void setDangerLevel(int lvl) {
        dangerLevel = lvl;
    }

    public void addDangerLevel(int amt) {
        dangerLevel = (int) Mth.clamp((long) dangerLevel + amt, 0, Integer.MAX_VALUE);
    }
}
