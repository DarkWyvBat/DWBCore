package net.darkwyvbat.dwbcore;

import net.darkwyvbat.dwbcore.debug.DwbDebugContent;
import net.darkwyvbat.dwbcore.event.DwbEvents;
import net.darkwyvbat.dwbcore.network.DwbEntityDataSerializers;
import net.darkwyvbat.dwbcore.registry.DwbRegistries;
import net.darkwyvbat.dwbcore.util.ModInfo;
import net.darkwyvbat.dwbcore.world.block.DwbBlocks;
import net.darkwyvbat.dwbcore.world.block.entity.DwbBlockEntityType;
import net.darkwyvbat.dwbcore.world.gen.DwbStructureProcessorType;
import net.darkwyvbat.dwbcore.world.gen.proxyblock.ProxyBlockActionOps;
import net.darkwyvbat.dwbcore.world.gen.proxyblock.ProxyBlockActionTypes;
import net.darkwyvbat.dwbcore.world.item.DwbItems;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DwbCore implements ModInitializer {

    public static final ModInfo INFO = new ModInfo("dwbcore", "1.0.0", "DWB Core");
    public static final Logger LOGGER = LoggerFactory.getLogger(INFO.id());
    public static final boolean IS_DEV = FabricLoader.getInstance().isDevelopmentEnvironment();

    @Override
    public void onInitialize() {
        LOGGER.info("DWB Core init...");

        if (IS_DEV)
            DwbDebugContent.init();

        DwbEntityDataSerializers.init();
        DwbRegistries.init();
        DwbEvents.init();
        DwbBlocks.init();
        DwbBlockEntityType.init();
        ProxyBlockActionTypes.init();
        DwbItems.init();
        DwbStructureProcessorType.init();
        ProxyBlockActionOps.init();
    }
}