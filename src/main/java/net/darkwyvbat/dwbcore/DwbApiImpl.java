package net.darkwyvbat.dwbcore;

import net.darkwyvbat.dwbcore.api.DwbApi;
import net.darkwyvbat.dwbcore.util.ModInfo;

public final class DwbApiImpl implements DwbApi {
    public static final DwbApi INSTANCE = new DwbApiImpl();

    private DwbApiImpl() {
    }


    @Override
    public ModInfo getInfo() {
        return DwbCore.INFO;
    }
}
