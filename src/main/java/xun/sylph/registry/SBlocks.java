package xun.sylph.registry;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CraftingTableBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;
import xun.sylph.Sylph;

import java.util.function.Supplier;

public class SBlocks {

    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(Sylph.MOD_ID);

    public static final DeferredBlock<Block> SPRUCE_CRAFTING_TABLE = registerBlock("spruce_crafting_table",
            () -> new CraftingTableBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.SPRUCE_PLANKS).strength(2.5F))
    );
    public static final DeferredBlock<Block> BIRCH_CRAFTING_TABLE = registerBlock("birch_crafting_table",
            () -> new CraftingTableBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.BIRCH_PLANKS).strength(2.5F))
    );

    private static <T extends Block> DeferredBlock<T> registerBlock(String name, Supplier<T> block) {
        DeferredBlock<T> toReturn = BLOCKS.register(name, block);
        registerBlockItem(name, toReturn);
        return toReturn;
    }

    private static <T extends Block> void registerBlockItem(String name, DeferredBlock<T> block) {
        SItems.ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties()));
    }
}
