package xun.sylph.data.generator;

import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import xun.sylph.Sylph;
import xun.sylph.data.provider.SBlockStateProvider;
import xun.sylph.registry.SBlocks;

public class SBlockStates extends SBlockStateProvider {

    public SBlockStates(PackOutput output, ExistingFileHelper exFileHelper) {
        super(output, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {

        craftingTable(SBlocks.SPRUCE_CRAFTING_TABLE, ResourceLocation.withDefaultNamespace("block/spruce_planks"));
        craftingTable(SBlocks.BIRCH_CRAFTING_TABLE, ResourceLocation.withDefaultNamespace("block/birch_planks"));
    }
}
