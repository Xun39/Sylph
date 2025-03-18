package xun.sylph;

import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Blocks;
import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import xun.sylph.registry.SBlocks;
import xun.sylph.registry.SItems;

@Mod(Sylph.MOD_ID)
public class Sylph {

    public static final String MOD_ID = "sylph";
    private static final Logger LOGGER = LogUtils.getLogger();

    public static ResourceLocation modLoc(String path) { return ResourceLocation.fromNamespaceAndPath(MOD_ID, path); }

    public Sylph(IEventBus modEventBus, ModContainer modContainer) {

        IEventBus neoEventBus = NeoForge.EVENT_BUS;

        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(this::addCreative);

        SItems.ITEMS.register(modEventBus);
        SBlocks.BLOCKS.register(modEventBus);

        neoEventBus.register(this);
        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {

    }

    private void addCreative(BuildCreativeModeTabContentsEvent event) {
        ResourceKey<CreativeModeTab> tab = event.getTabKey();

        if (tab == CreativeModeTabs.FUNCTIONAL_BLOCKS) {
            insertAfter(event, Blocks.CRAFTING_TABLE, SBlocks.SPRUCE_CRAFTING_TABLE);
            insertAfter(event, SBlocks.SPRUCE_CRAFTING_TABLE, SBlocks.BIRCH_CRAFTING_TABLE);
        }
    }

    private static void insertAfter(BuildCreativeModeTabContentsEvent event, ItemLike existingEntry, ItemLike newEntry) {
        event.insertAfter(new ItemStack(existingEntry), new ItemStack(newEntry), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {

    }
}
