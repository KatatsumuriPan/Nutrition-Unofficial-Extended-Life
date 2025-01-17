package ca.wescook.nutrition.compat;

import net.minecraftforge.common.config.Configuration;

import ca.wescook.nutrition.compat.ic2.CompatIC2Ex;

public class CompatManager {

    private static final String CATEGORY_COMPAT = "Mod Compat";

    private static boolean ic2;

    public static void registerConfigs(Configuration configFile) {
        ic2 = configFile.getBoolean("IC2", CATEGORY_COMPAT, true,
                "Enable IC2 compatibility. (for Filled Tin Can)");
    }

    public static void initCompat() {
        if (ic2)
            CompatIC2Ex.init();
    }
}
