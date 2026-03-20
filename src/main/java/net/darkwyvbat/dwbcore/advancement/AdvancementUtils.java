package net.darkwyvbat.dwbcore.advancement;

import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;

public class AdvancementUtils {
    public static boolean award(ServerPlayer serverPlayer, Identifier id) {
        return award(serverPlayer, id, "manual_unlock");
    }

    public static boolean award(ServerPlayer serverPlayer, Identifier id, String criteria) {
        AdvancementHolder advancement = serverPlayer.level().getServer().getAdvancements().get(id);
        return advancement != null && serverPlayer.getAdvancements().award(advancement, criteria);
    }
}