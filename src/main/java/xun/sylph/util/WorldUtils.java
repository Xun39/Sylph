package xun.sylph.util;

import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.TrapDoorBlock;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.List;

public class WorldUtils {

    public static Level getLevel(LevelAccessor levelAccessor) {
        if (levelAccessor.isClientSide()) {
            return null;
        }
        if (levelAccessor instanceof Level) {
            return ((Level) levelAccessor);
        }
        return null;
    }

    public static List<BlockPos> getBlocksAround(BlockPos pos, boolean down) {
        List<BlockPos> around = new ArrayList<BlockPos>();
        around.add(pos.north());
        around.add(pos.east());
        around.add(pos.south());
        around.add(pos.west());
        around.add(pos.above());
        if (down) {
            around.add(pos.below());
        }
        return around;
    }

    public static Boolean withinDistance(BlockPos start, BlockPos end, int distance) {
        return withinDistance(start, end, (double) distance);
    }
    public static Boolean withinDistance(BlockPos start, BlockPos end, double distance) {
        return start.closerThan(end, distance);
    }

    public static boolean canOpenByHand(BlockState blockState) {
        return canOpenByHand(blockState, true);
    }
    public static boolean canOpenByHand(BlockState state, boolean defaultReturn) {
        Block block = state.getBlock();
        if (block instanceof DoorBlock doorBlock) {
            return doorBlock.type().canOpenByHand();
        }
        else if (block instanceof TrapDoorBlock trapDoorBlock) {
            return state.is(BlockTags.WOODEN_TRAPDOORS);
        }

        return defaultReturn;
    }
}
