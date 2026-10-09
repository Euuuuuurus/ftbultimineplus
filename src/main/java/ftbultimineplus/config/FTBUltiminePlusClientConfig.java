package ftbultimineplus.config;

import dev.ftb.mods.ftblibrary.snbt.config.ConfigUtil;
import dev.ftb.mods.ftblibrary.snbt.config.IntValue;
import dev.ftb.mods.ftblibrary.snbt.config.SNBTConfig;

/**
 * Client-side configuration (config/ftbultimineplus-client.snbt).
 * Ported from FTB Ultimine 2101.1.x client config key "preview_line_alpha".
 */
public interface FTBUltiminePlusClientConfig {
    SNBTConfig CONFIG = (SNBTConfig) SNBTConfig.create("ftbultimineplus-client")
            .comment(new String[]{
                    "Client-specific configuration for FTB Ultimine Plus",
                    "Changes to this file require you to reload the world"
            });

    IntValue previewLineAlpha = CONFIG.getInt("preview_line_alpha", 10)
            .range(0, 255)
            .comment(new String[]{"Alpha value for the translucent preview outline lines (0 = invisible, 255 = opaque)"});

    static void load() {
        ConfigUtil.loadDefaulted(CONFIG, ConfigUtil.LOCAL_DIR, "ftbultimineplus");
    }
}
