package net.darkwyvbat.dwbcore.mixin;

import net.darkwyvbat.dwbcore.lowzone.NodeExtension;
import net.darkwyvbat.dwbcore.world.entity.ai.nav.DwbPathType;
import net.minecraft.world.level.pathfinder.Node;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(Node.class)
public class NodeMixin implements NodeExtension {

    @Unique
    private DwbPathType dwbcore_type = DwbPathType.NONE;

    @Override
    public void dwbcore_setType(DwbPathType type) {
        dwbcore_type = type;
    }

    @Override
    public DwbPathType dwbcore_getType() {
        return dwbcore_type;
    }
}