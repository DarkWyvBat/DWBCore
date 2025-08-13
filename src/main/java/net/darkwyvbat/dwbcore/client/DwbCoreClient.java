package net.darkwyvbat.dwbcore.client;

import net.darkwyvbat.dwbcore.DwbCore;
import net.darkwyvbat.dwbcore.debug.DwbDebugContent;
import net.fabricmc.api.ClientModInitializer;

public class DwbCoreClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        if (DwbCore.IS_DEV)
            DwbDebugContent.registerClient();
    }
}
