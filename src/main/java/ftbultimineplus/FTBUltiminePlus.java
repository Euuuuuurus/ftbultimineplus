package ftbultimineplus;

import dev.ftb.mods.ftbultimine.shape.Shape;
import ftbultimineplus.config.FTBUltiminePlusClientConfig;
import ftbultimineplus.config.FTBUltiminePlusServerConfig;
import ftbultimineplus.shape.LargeTunnelShape;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Mixins;

/**
 * FTB Ultimine Plus - backport of newer FTB Ultimine features to Minecraft 1.18.2 Forge.
 *
 * This mod does NOT modify the FTB Ultimine jar; it hooks into FTB Ultimine 1802.3.x via
 * Mixin (see mixin package) and extends it with:
 *   - right-click axe strip / shovel flatten (ported from FTB Ultimine 2101.1.x rightclick handlers)
 *   - the large (3x3) tunnel shape (ported from FTB Ultimine 2101.1.x)
 *   - configurable preview outline alpha
 */
@Mod(FTBUltiminePlus.MOD_ID)
public class FTBUltiminePlus {
    public static final String MOD_ID = "ftbultimineplus";
    public static final Logger LOGGER = LogManager.getLogger();

    public FTBUltiminePlus() {
        Mixins.addConfiguration("mixins." + MOD_ID + ".json");

        // Register the backported large tunnel shape. FTB Ultimine 1802.3.x already called
        // Shape.postinit() in its constructor; call it again here to be safe regardless of
        // constructor ordering (postinit is idempotent - it rebuilds the next/prev cycle).
        Shape.register(new LargeTunnelShape());
        Shape.postinit();

        MinecraftForge.EVENT_BUS.addListener(this::onServerStarting);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onClientSetup);
    }

    private void onServerStarting(ServerStartingEvent event) {
        FTBUltiminePlusServerConfig.load(event.getServer());

        // Self-check (server startup, after all mod constructors ran): the shape must be
        // registered and the next/prev cycle must be a complete loop covering all shapes.
        Shape largeTunnel = Shape.get("large_tunnel");
        int cycleLength = 0;
        if (largeTunnel != null) {
            Shape cursor = largeTunnel;
            do {
                cycleLength++;
                cursor = cursor.next;
            } while (cursor != largeTunnel && cycleLength < 1000);
        }
        if (largeTunnel == null || cycleLength < 6) {
            LOGGER.error("[self-check] FAILED: large_tunnel registered={}, shape cycle length={} (expected a full loop of 6)", largeTunnel != null, cycleLength);
        } else {
            LOGGER.info("[self-check] OK: large_tunnel registered, shape cycle length={}", cycleLength);
        }
    }

    private void onClientSetup(FMLClientSetupEvent event) {
        FTBUltiminePlusClientConfig.load();
    }
}
