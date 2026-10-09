package ftbultimineplus.config;

import dev.ftb.mods.ftblibrary.snbt.config.BooleanValue;
import dev.ftb.mods.ftblibrary.snbt.config.ConfigUtil;
import dev.ftb.mods.ftblibrary.snbt.config.SNBTConfig;
import net.minecraft.server.MinecraftServer;

/**
 * Server-side configuration, stored per-world in serverconfig/ftbultimineplus-server.snbt.
 * Uses the FTB Library 1.18.2 SNBT API (getGroup / getBoolean), same keys as
 * FTB Ultimine 2101.1.x (features.right_click_axe / right_click_shovel).
 */
public interface FTBUltiminePlusServerConfig {
    SNBTConfig CONFIG = (SNBTConfig) SNBTConfig.create("ftbultimineplus-server")
            .comment(new String[]{
                    "Server-specific configuration for FTB Ultimine Plus",
                    "Modpack defaults should be defined in <instance>/config/ftbultimineplus-server.snbt",
                    "  (may be overwritten on modpack update)",
                    "Server admins may locally override this by copying into <instance>/world/serverconfig/ftbultimineplus-server.snbt",
                    "  (will NOT be overwritten on modpack update)"
            });
    SNBTConfig FEATURES = CONFIG.getGroup("features");

    BooleanValue RIGHT_CLICK_AXE = FEATURES.getBoolean("right_click_axe", true)
            .comment(new String[]{"Right-click with an axe with the Ultimine key held to strip multiple logs and scrape/unwax copper blocks"});
    BooleanValue RIGHT_CLICK_SHOVEL = FEATURES.getBoolean("right_click_shovel", true)
            .comment(new String[]{"Right-click with a shovel with the Ultimine key held to flatten multiple grass/dirt blocks into dirt paths"});

    static void load(MinecraftServer server) {
        ConfigUtil.loadDefaulted(CONFIG, server.getWorldPath(ConfigUtil.SERVER_CONFIG_DIR), "ftbultimineplus");
    }
}
