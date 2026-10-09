package ftbultimineplus.mixin;

import dev.ftb.mods.ftbultimine.client.FTBUltimineClient;
import ftbultimineplus.config.FTBUltiminePlusClientConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

/**
 * Client-side rendering hook: makes the translucent preview outline alpha configurable
 * (FTB Ultimine 1802.3.x hard-codes 10; ported from FTB Ultimine 2101.1.x
 * client config "preview_line_alpha").
 *
 * In {@code renderInGame(PoseStack)} the outline is drawn as two passes: an opaque white pass
 * ({@code color(255,255,255,255)}) and a translucent pass ({@code color(255,255,255,10)}).
 * This replaces the two occurrences of the literal constant 10 (the translucent pass alpha).
 *
 * IMPORTANT: this targets a constant, not a method call. FTB Ultimine's published jar runs in the
 * SRG-named production environment where {@code VertexConsumer.color} is {@code m_6122_}, so a
 * method-targeted injection (remap=false) would fail there; a constant match is name-independent
 * and works in both the dev (official names) and production (SRG) environments.
 *
 * All targets are mod classes (never obfuscated on 1.17+), so remap = false and no refmap is needed.
 */
@Mixin(value = FTBUltimineClient.class, remap = false)
public abstract class UltimineClientRenderMixin {
    @ModifyConstant(method = "renderInGame",
            constant = @Constant(intValue = 10),
            remap = false)
    private int ftbultimineplus$previewLineAlpha(int value) {
        return FTBUltiminePlusClientConfig.previewLineAlpha.get();
    }
}
