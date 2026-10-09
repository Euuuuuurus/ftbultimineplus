package ftbultimineplus.mixin;

import com.mojang.blaze3d.vertex.VertexConsumer;
import dev.ftb.mods.ftbultimine.client.FTBUltimineClient;
import ftbultimineplus.config.FTBUltiminePlusClientConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

/**
 * Client-side rendering hook: makes the translucent preview outline alpha configurable
 * (FTB Ultimine 1802.3.x hard-codes 10; ported from FTB Ultimine 2101.1.x
 * client config "preview_line_alpha").
 *
 * In {@code renderInGame(PoseStack)} the outline is drawn as two passes: an opaque white pass
 * ({@code color(255,255,255,255)}) and a translucent pass ({@code color(255,255,255,10)}).
 * This modifies only the second {@code VertexConsumer#color} call site (ordinal 1).
 *
 * All targets are mod classes (never obfuscated on 1.17+), so remap = false and no refmap is needed.
 */
@Mixin(value = FTBUltimineClient.class, remap = false)
public abstract class UltimineClientRenderMixin {
    @ModifyArg(method = "renderInGame",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/mojang/blaze3d/vertex/VertexConsumer;color(IIII)Lcom/mojang/blaze3d/vertex/VertexConsumer;",
                    ordinal = 1
            ),
            index = 3,
            remap = false)
    private int ftbultimineplus$previewLineAlpha(int alpha) {
        return FTBUltiminePlusClientConfig.previewLineAlpha.get();
    }
}
