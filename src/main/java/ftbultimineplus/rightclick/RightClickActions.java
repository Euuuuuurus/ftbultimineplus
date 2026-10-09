package ftbultimineplus.rightclick;

import ftbultimineplus.config.FTBUltiminePlusServerConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ToolActions;

import java.util.List;

/**
 * Bulk right-click actions, ported from FTB Ultimine 2101.1.x rightclick handlers
 * (AxeStripping / ShovelFlattening): with the Ultimine key held, right-clicking applies
 * the main-hand item's use-on behaviour to every cached block position, exactly like
 * the vanilla single-block right-click. Works with any mod that implements
 * canPerformAction (vanilla axes / shovels, and modded equivalents).
 */
public final class RightClickActions {
    private RightClickActions() {
    }

    /**
     * @return number of blocks the right-click was successfully applied to, 0 if nothing was done
     */
    public static int handleRightClick(ServerPlayer player, InteractionHand hand, List<BlockPos> positions) {
        ItemStack stack = player.getItemInHand(hand);
        boolean axe = FTBUltiminePlusServerConfig.RIGHT_CLICK_AXE.get()
                && stack.getItem().canPerformAction(stack, ToolActions.AXE_STRIP);
        boolean shovel = !axe && FTBUltiminePlusServerConfig.RIGHT_CLICK_SHOVEL.get()
                && stack.getItem().canPerformAction(stack, ToolActions.SHOVEL_FLATTEN);

        if (!axe && !shovel) {
            return 0;
        }

        int clicked = 0;
        for (BlockPos pos : positions) {
            // Same synthetic hit as the upstream handlers: top face of the block below the cursor.
            BlockHitResult hitResult = new BlockHitResult(Vec3.atBottomCenterOf(pos.above()), Direction.UP, pos, false);
            if (stack.useOn(new UseOnContext(player, hand, hitResult)).consumesAction()) {
                clicked++;
            }
        }
        return clicked;
    }
}
