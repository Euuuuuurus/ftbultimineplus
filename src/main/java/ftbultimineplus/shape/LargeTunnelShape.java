package ftbultimineplus.shape;

import dev.ftb.mods.ftbultimine.shape.Shape;
import dev.ftb.mods.ftbultimine.shape.ShapeContext;
import net.minecraft.core.BlockPos;

import java.util.ArrayList;
import java.util.List;

/**
 * Large (3x3) tunnel shape, ported from FTB Ultimine 2101.1.x LargeTunnelShape.
 * Starts at the clicked block and digs a 3x3 tunnel along the face axis, one layer per
 * iteration (repeating the layer's corners, then moving one block deeper).
 */
public class LargeTunnelShape extends Shape {
    @Override
    public String getName() {
        return "large_tunnel";
    }

    @Override
    public List<BlockPos> getBlocks(ShapeContext context) {
        List<BlockPos> list = new ArrayList<>(9);
        BlockPos basePos = context.pos;
        list.add(basePos);

        for (int depth = 0; depth < Integer.MAX_VALUE && list.size() < context.maxBlocks; depth++) {
            int size = list.size();

            outer:
            for (int a = -1; a <= 1; a++) {
                for (int b = -1; b <= 1; b++) {
                    if (depth > 0 || a != 0 || b != 0) {
                        BlockPos pos = switch (context.face.getAxis()) {
                            case X -> basePos.offset(0, a, b);
                            case Y -> basePos.offset(a, 0, b);
                            case Z -> basePos.offset(a, b, 0);
                        };
                        if (context.check(pos)) {
                            list.add(pos);
                            if (list.size() >= context.maxBlocks) {
                                break outer;
                            }
                        }
                    }
                }
            }

            if (list.size() == size) {
                break;
            }
            basePos = basePos.relative(context.face.getOpposite());
        }

        return list;
    }
}
