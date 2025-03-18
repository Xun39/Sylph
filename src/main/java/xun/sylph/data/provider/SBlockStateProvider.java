package xun.sylph.data.provider;

import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.client.model.generators.ConfiguredModel;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.registries.DeferredBlock;
import xun.sylph.Sylph;

public abstract class SBlockStateProvider extends BlockStateProvider {

    public static String VCT_LOCATION_PATH = "block/crafting_table_variants/";

    public SBlockStateProvider(PackOutput output, ExistingFileHelper exFileHelper) {
        super(output, Sylph.MOD_ID, exFileHelper);
    }

    protected void blockItem(DeferredBlock<?> deferredBlock) {
        simpleBlockItem(deferredBlock.get(), new ModelFile.UncheckedModelFile(Sylph.modLoc("block/" + getBlockRegistryName(deferredBlock))));
    }

    protected void craftingTable(DeferredBlock<Block> block, ResourceLocation bottomTextureLocation) {
        getVariantBuilder(block.get()).forAllStates(state -> ConfiguredModel.builder().modelFile(models().withExistingParent(
                getBlockRegistryName(block), "cube")
                .texture("down", bottomTextureLocation)
                .texture("up", blockLocation(block, VCT_LOCATION_PATH, "_top"))
                .texture("north", blockLocation(block, VCT_LOCATION_PATH, "_top"))
                .texture("particle", blockLocation(block, VCT_LOCATION_PATH, "_top"))
                .texture("south", blockLocation(block, VCT_LOCATION_PATH, "_side"))
                .texture("east", blockLocation(block, VCT_LOCATION_PATH, "_side"))
                .texture("west", blockLocation(block, VCT_LOCATION_PATH, "_front"))
        ).build());
        blockItem(block);
    }

    protected static ResourceLocation blockLocation(DeferredBlock<Block> block, String loc, String suffix) {
        return Sylph.modLoc(loc + getBlockRegistryName(block) + suffix);
    }

    protected static String getBlockRegistryName(DeferredBlock<?> deferredBlock) {
        return deferredBlock.getId().getPath();
    }
}
