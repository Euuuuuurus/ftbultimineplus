package ftbultimineplus.mixin;

import dev.architectury.event.EventResult;
import dev.ftb.mods.ftbultimine.FTBUltimine;
import dev.ftb.mods.ftbultimine.FTBUltiminePlayerData;
import dev.ftb.mods.ftbultimine.shape.ShapeContext;
import ftbultimineplus.rightclick.RightClickActions;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.HitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

/**
 * Hooks FTB Ultimine 1802.3.x {@code blockRightClick} (official mapping, 1.18.2 signature:
 * {@code EventResult blockRightClick(Player, InteractionHand, BlockPos, Direction)}) right after
 * {@code updateBlocks(...)} is assigned. Locals captured in order:
 * serverPlayer, result (HitResult), data (FTBUltiminePlayerData), shapeContext (ShapeContext).
 *
 * When the player is holding an axe (axe strip) or shovel (flatten) while Ultimine is pressed,
 * the main-hand item's use-on behaviour is applied to every cached block (ported from FTB Ultimine
 * 2101.1.x AxeStripping / ShovelFlattening). If any block was affected, the right-click is
 * consumed, skipping FTB Ultimine's own hoe/crop handling.
 *
 * All targets are mod classes (never obfuscated on 1.17+), so remap = false and no refmap is needed.
 */
@Mixin(value = FTBUltimine.class, remap = false)
public abstract class UltimineRightClickMixin {
    @Inject(method = "blockRightClick",
            at = @At(
                    value = "INVOKE_ASSIGN",
                    target = "Ldev/ftb/mods/ftbultimine/FTBUltiminePlayerData;updateBlocks(Lnet/minecraft/server/level/ServerPlayer;Lnet/minecraft/core/BlockPos;Lnet/minecraft/core/Direction;ZI)Ldev/ftb/mods/ftbultimine/shape/ShapeContext;"
            ),
            cancellable = true,
            locals = LocalCapture.CAPTURE_FAILHARD,
            remap = false)
    private void ftbultimineplus$axeAndShovel(
            Player player,
            InteractionHand hand,
            BlockPos clickPos,
            Direction face,
            CallbackInfoReturnable<EventResult> cir,
            ServerPlayer serverPlayer,
            HitResult result,
            FTBUltiminePlayerData data,
            ShapeContext shapeContext) {
        if (shapeContext != null && data.pressed && data.cachedBlocks != null && !data.cachedBlocks.isEmpty()) {
            int didWork = RightClickActions.handleRightClick(serverPlayer, hand, data.cachedBlocks);
            if (didWork > 0) {
                player.swing(hand);
                cir.setReturnValue(EventResult.interruptFalse());
            }
        }
    }
}
