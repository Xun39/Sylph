package xun.sylph.data.generator;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;
import xun.sylph.Sylph;
import xun.sylph.registry.SBlocks;

import java.util.concurrent.CompletableFuture;

public class SBlockTags extends BlockTagsProvider {

    public SBlockTags(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, Sylph.MOD_ID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {

        tag(BlockTags.MINEABLE_WITH_AXE).add(
                SBlocks.SPRUCE_CRAFTING_TABLE.get(),
                SBlocks.BIRCH_CRAFTING_TABLE.get()
        );

        tag(Tags.Blocks.PLAYER_WORKSTATIONS_CRAFTING_TABLES).add(
                SBlocks.SPRUCE_CRAFTING_TABLE.get(),
                SBlocks.BIRCH_CRAFTING_TABLE.get()
        );
    }
}
