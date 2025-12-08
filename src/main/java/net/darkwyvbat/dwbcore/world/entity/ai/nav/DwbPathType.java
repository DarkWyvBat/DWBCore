package net.darkwyvbat.dwbcore.world.entity.ai.nav;

import net.minecraft.world.level.pathfinder.PathType;
import org.jetbrains.annotations.Nullable;

public class DwbPathType {
    public static final DwbPathType NONE = new DwbPathType(PathType.WALKABLE);

    private final PathType fallback;
    private float malus = 0.0F;
    private boolean isClimb = false;
    private boolean isCrouch = false;
    private boolean isDanger = false;
    private boolean isBreach = false;
    private byte selfSate = 0;

    @Nullable
    private String data = null;

    public DwbPathType(PathType fallback) {
        this.fallback = fallback;
    }

    public DwbPathType malus(float malus) {
        this.malus = malus;
        return this;
    }

    public DwbPathType climb() {
        isClimb = true;
        return this;
    }

    public DwbPathType crouch() {
        isCrouch = true;
        return this;
    }

    public DwbPathType danger() {
        isDanger = true;
        return this;
    }

    public DwbPathType breach() {
        isBreach = true;
        return this;
    }

    public DwbPathType selfRate(byte v) {
        selfSate = v;
        return this;
    }

    public void setData(@Nullable String data) {
        this.data = data;
    }

    public PathType getFallback() {
        return fallback;
    }

    public float getMalus() {
        return malus;
    }

    public boolean isClimb() {
        return isClimb;
    }

    public boolean isCrouch() {
        return isCrouch;
    }

    public boolean isDanger() {
        return isDanger;
    }

    public boolean isBreach() {
        return isBreach;
    }

    public byte getSelfSate() {
        return selfSate;
    }

    public @Nullable String getData() {
        return data;
    }
}
