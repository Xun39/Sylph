package xun.sylph.data;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import xun.sylph.Sylph;
import xun.sylph.data.generator.SBlockStates;
import xun.sylph.data.generator.SBlockTags;

import java.util.concurrent.CompletableFuture;

@EventBusSubscriber(modid = Sylph.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public class DataGenerators {

    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {

        DataGenerator generator = event.getGenerator();
        PackOutput output = generator.getPackOutput();
        CompletableFuture<HolderLookup.Provider> registries = event.getLookupProvider();
        ExistingFileHelper helper = event.getExistingFileHelper();
        boolean client = event.includeClient();
        boolean server = event.includeServer();

        BlockTagsProvider blockTagsProvider = new SBlockTags(output, registries, helper);

        generator.addProvider(client, new SBlockStates(output, helper));
        //generator.addProvider(client, new UOItemModels(output, helper));

        //generator.addProvider(server, new UOAdvancements(output, registries, helper));

        //generator.addProvider(server, new UOLootTables(output, registries));
        //generator.addProvider(server, new UORecipes(output, registries));
        generator.addProvider(server, blockTagsProvider);
        //generator.addProvider(server, new UOItemTags(output, registries, blockTagsProvider.contentsGetter(), helper));
        //generator.addProvider(server, new UOEntityTypeTags(output, registries, helper));
        //generator.addProvider(server, new UOBiomeTags(output, registries, helper));

        //generator.addProvider(server, new UOGlobalLootModifiers(output, registries));
        //generator.addProvider(server, new UODatapackEntries(output, registries));

    }
}
