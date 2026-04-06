package net.darkwyvbat.dwbcore.world.entity.ai.nav;

import net.minecraft.world.level.pathfinder.PathType;

public class DwbPathTypes {
    public static final DwbPathType CROUCH = new DwbPathType(PathType.WALKABLE)
            .crouch()
            .malus(2);
    public static final DwbPathType CLIMB = new DwbPathType(PathType.WALKABLE)
            .climb()
            .malus(6);
    public static final DwbPathType HUMANOID = new DwbPathType(PathType.WALKABLE)
            .crouch()
            .climb()
            .malus(6);
}