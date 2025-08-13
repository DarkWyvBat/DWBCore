package net.darkwyvbat.dwbcore.world.entity.ai.perception;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.Mth;

public class PerceptionProfile {
    private final int attitude;
    private int activityLevel;  // 0..20 chill, 20..40 regular actions, 40..60 danger fight etc, 60.. panic or active actions
    private int dangerLevel;

    public static final Codec<PerceptionProfile> CODEC = RecordCodecBuilder.create(i ->
            i.group(
                    Codec.INT.fieldOf("Attitude").forGetter(PerceptionProfile::getAttitude),
                    Codec.INT.fieldOf("ActivityLevel").forGetter(PerceptionProfile::getActivityLevel)
            ).apply(i, PerceptionProfile::new)
    );

    public PerceptionProfile(int attitude, int activityLevel) {
        this.attitude = attitude;
        this.activityLevel = activityLevel;
    }

    public ActivityState getState() {
        return ActivityState.from(this.activityLevel);
    }

    public boolean is(ActivityState state) {
        return this.getState() == state;
    }

    public int getActivityLevel() {
        return activityLevel;
    }

    public void setActivityLevel(int lvl) {
        activityLevel = Mth.clamp(lvl, 0, Integer.MAX_VALUE);
    }

    public void addActivityLevel(int amt) {
        activityLevel = (int) Mth.clamp((long) activityLevel + amt, 0, Integer.MAX_VALUE);
    }

    public void addActivityIfLess(int amt, int lim) {
        if (activityLevel < lim) activityLevel += amt;
    }

    public void reduceActivity(int amt) {
        activityLevel = Math.max(0, activityLevel - amt);
    }

    public int getAttitude() {
        return attitude;
    }

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
