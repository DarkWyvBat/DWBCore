package net.darkwyvbat.dwbcore.api;

import net.darkwyvbat.dwbcore.DwbApiImpl;
import net.darkwyvbat.dwbcore.util.ModInfo;

public interface DwbApi {
    static DwbApi getInstance() {
        return DwbApiImpl.INSTANCE;
    }

    ModInfo getInfo();
}
