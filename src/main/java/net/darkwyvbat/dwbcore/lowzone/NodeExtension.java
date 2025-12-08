package net.darkwyvbat.dwbcore.lowzone;

import net.darkwyvbat.dwbcore.world.entity.ai.nav.DwbPathType;

public interface NodeExtension {
    void dwbcore_setType(DwbPathType type);

    DwbPathType dwbcore_getType();
}