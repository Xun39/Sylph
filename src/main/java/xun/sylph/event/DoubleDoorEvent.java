package xun.sylph.event;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.level.BlockEvent;
import xun.sylph.Sylph;
import xun.sylph.util.WorldUtils;

import java.util.*;

@EventBusSubscriber(modid = Sylph.MOD_ID, bus = EventBusSubscriber.Bus.GAME)
public class DoubleDoorEvent {

    private static final List<BlockPos> prevPoweredPos = new ArrayList<BlockPos>();
    private static final HashMap<BlockPos, Integer> prevButtonPos = new HashMap<BlockPos, Integer>();

    @SubscribeEvent
    public static void onNeighbourNotice(BlockEvent.NeighborNotifyEvent event) {

        Level level = WorldUtils.getLevel(event.getLevel());
        BlockState state = event.getState();
        BlockPos pos = event.getPos();

        if (level == null) {
            return;
        }

        if (level.isClientSide) {
            return;
        }

        BooleanProperty isPoweredProperty = BlockStateProperties.POWERED;
        IntegerProperty weightPowerProperty = BlockStateProperties.POWER;

        Block block = state.getBlock();

        if (!(block instanceof PressurePlateBlock) && !(block instanceof WeightedPressurePlateBlock)) {
            if (!(block instanceof ButtonBlock) && !(block instanceof LeverBlock)) {
                return;
            }
            else {
                if (prevButtonPos.containsKey(pos)) {
                    prevButtonPos.remove(pos);
                }
                else {
                    prevButtonPos.put(pos.immutable(), 1);
                    return;
                }

                if (!state.getValue(isPoweredProperty)) {
                    if (!prevPoweredPos.contains(pos)) {
                        return;
                    }
                    prevPoweredPos.remove(pos);
                }
            }
        }
        else if (block instanceof WeightedPressurePlateBlock) {
            if (state.getValue(weightPowerProperty) == 0) {
                if (!prevPoweredPos.contains(pos)) {
                    return;
                }
            }
        }
        else {
            if (!state.getValue(isPoweredProperty)) {
                if (!prevPoweredPos.contains(pos)) {
                    return;
                }
            }
        }

        boolean blockStateProp;
        if (block instanceof WeightedPressurePlateBlock) {
            blockStateProp = state.getValue(weightPowerProperty) > 0;
        }
        else {
            blockStateProp = state.getValue(isPoweredProperty);
        }

        int radius = 1;

        BlockPos doorBlockPos = null;

        for (BlockPos aroundPos : WorldUtils.getBlocksAround(pos, false)) {
            BlockState oBlockState = level.getBlockState(aroundPos);
            if (isDoorBlock(oBlockState)) {
                doorBlockPos = aroundPos.immutable();
                break;
            }
        }

        if (doorBlockPos == null) {
            for (BlockPos aroundPos : BlockPos.betweenClosed(pos.getX() - radius, pos.getY() - 1, pos.getZ() - radius, pos.getX() + radius, pos.getY() + 1, pos.getZ() + radius)) {
                BlockState oBlockState = level.getBlockState(aroundPos);
                if (isDoorBlock(oBlockState)) {
                    doorBlockPos = aroundPos;
                    break;
                }
            }
        }


        if (doorBlockPos != null) {
            if (processDoor(null, level, doorBlockPos, level.getBlockState(doorBlockPos), blockStateProp)) {
                if (blockStateProp) {
                    prevPoweredPos.add(pos.immutable());
                }
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerInteract(PlayerInteractEvent.RightClickBlock event) {
        Level level = event.getLevel();
        BlockPos pos = event.getPos();
        BlockState state = level.getBlockState(pos);
        Player player = event.getEntity();

        if (!isDoorBlock(state) || player.isCrouching() || event.getHand() != InteractionHand.MAIN_HAND) {
            return;
        }

        onDoorClick(level, player, event.getHand(), pos, event.getHitVec());
    }

    public static void onDoorClick(Level level, Player player, InteractionHand interactionHand, BlockPos blockPos, BlockHitResult blockHitResult) {
        if (level.isClientSide || interactionHand != InteractionHand.MAIN_HAND || player.isCrouching()) {
            return;
        }

        BlockState clickState = level.getBlockState(blockPos);

        if (!WorldUtils.canOpenByHand(clickState)) {
            return;
        }

        processDoor(player, level, blockPos, clickState, null);
    }

    public static boolean processDoor(Player player, Level level, BlockPos blockPos, BlockState blockState, Boolean isOpen) {
        Block block = blockState.getBlock();
        if (block instanceof DoorBlock && blockState.getValue(DoorBlock.HALF) == DoubleBlockHalf.UPPER) {
            blockPos = blockPos.below();
            blockState = level.getBlockState(blockPos);
            block = blockState.getBlock();
        }

        if (isOpen == null) {
            isOpen = !blockState.getValue(BlockStateProperties.OPEN);
        }

        int yOffset = (block instanceof DoorBlock) ? 0 : 1;

        List<BlockPos> posToOpenList = recursivelyOpenDoors(new ArrayList<>(List.of(blockPos)), new ArrayList<>(), level, blockPos, blockPos, block, yOffset);

        if (posToOpenList.isEmpty()) {
            return false;
        }

        for (BlockPos toOpenBlockPos : posToOpenList) {
            BlockState oBlockState = level.getBlockState(toOpenBlockPos);
            Block oBlock = oBlockState.getBlock();

            if (oBlock instanceof DoorBlock) {
                level.setBlock(toOpenBlockPos, oBlockState.setValue(DoorBlock.OPEN, isOpen), 10);
            } else if (oBlock instanceof TrapDoorBlock) {
                level.setBlock(toOpenBlockPos, oBlockState.setValue(BlockStateProperties.OPEN, isOpen), 10);
            } else if (oBlock instanceof FenceGateBlock) {
                level.setBlock(toOpenBlockPos, oBlockState.setValue(FenceGateBlock.OPEN, isOpen), 10);
            }
        }

        return true;
    }

    private static List<BlockPos> recursivelyOpenDoors(List<BlockPos> posToOpenList, List<BlockPos> ignoreOpenList, Level level, BlockPos originalBlockPos, BlockPos blockPos, Block block, int yOffset) {
        BlockPos.betweenClosedStream(blockPos.offset(-1, -yOffset, -1), blockPos.offset(1, yOffset, 1))
                .forEach(bpa -> {
                    BlockPos immutableBpa = bpa.immutable();
                    if (posToOpenList.contains(immutableBpa) || ignoreOpenList.contains(immutableBpa)) {
                        return;
                    }

                    if (!WorldUtils.withinDistance(originalBlockPos, immutableBpa, 2)) {
                        return;
                    }

                    BlockState oBlockState = level.getBlockState(immutableBpa);
                    if (isDoorBlock(oBlockState) && oBlockState.getBlock() == block) {
                        posToOpenList.add(immutableBpa);
                        recursivelyOpenDoors(posToOpenList, ignoreOpenList, level, originalBlockPos, immutableBpa, block, yOffset);
                    } else {
                        ignoreOpenList.add(immutableBpa);
                    }
                });

        return posToOpenList;
    }

    public static boolean isDoorBlock(BlockState blockState) {
        Block block = blockState.getBlock();
        return (block instanceof DoorBlock /*&& ConfigHandler.enableDoors*/)
                || (block instanceof TrapDoorBlock /*&& ConfigHandler.enableTrapdoors*/)
                || (block instanceof FenceGateBlock /*&& ConfigHandler.enableFenceGates*/);
    }

    public static boolean isPressureBlock(BlockState blockState) {
        Block block = blockState.getBlock();
        if (block instanceof WeightedPressurePlateBlock) {
            return blockState.getValue(BlockStateProperties.POWER) > 0;
        }
        if (block instanceof PressurePlateBlock || block instanceof ButtonBlock) {
            return blockState.getValue(BlockStateProperties.POWERED);
        }
        return false;
    }
}
