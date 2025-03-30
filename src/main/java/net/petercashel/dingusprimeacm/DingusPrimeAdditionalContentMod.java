package net.petercashel.dingusprimeacm;

import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.event.server.ServerStartedEvent;
import net.neoforged.neoforge.items.ComponentItemHandler;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.HandlerThread;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import net.petercashel.dingusprimeacm.datagen.DataGeneration;
import net.petercashel.dingusprimeacm.kubejs.types.gameboy.container.GameboyInventoryMenu;
import net.petercashel.dingusprimeacm.kubejs.types.gameboy.container.GameboyMenu;
import net.petercashel.dingusprimeacm.kubejs.types.gameboy.datacomponent.CartItemDataComponent;
import net.petercashel.dingusprimeacm.kubejs.types.gameboy.item.GameBoyItem;
import net.petercashel.dingusprimeacm.networking.PacketHandler;
import net.petercashel.dingusprimeacm.world.WorldDataManager;
import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

// The value here should match an entry in the META-INF/neoforge.mods.toml file
@Mod(DingusPrimeAdditionalContentMod.MODID)
public class DingusPrimeAdditionalContentMod
{
    // Define mod id in a common place for everything to reference
    public static final String MODID = "dingusprimeacm";
    // Directly reference a slf4j logger
    private static final Logger LOGGER = LogUtils.getLogger();
    // Create a Deferred Register to hold Blocks which will all be registered under the "dingusprimeacm" namespace
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(MODID);
    // Create a Deferred Register to hold Items which will all be registered under the "dingusprimeacm" namespace
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MODID);
    // Create a Deferred Register to hold CreativeModeTabs which will all be registered under the "dingusprimeacm" namespace
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);

    // Creates a new Block with the id "dingusprimeacm:example_block", combining the namespace and path
    public static final DeferredBlock<Block> EXAMPLE_BLOCK = BLOCKS.registerSimpleBlock("example_block", BlockBehaviour.Properties.of().mapColor(MapColor.STONE));
    // Creates a new BlockItem with the id "dingusprimeacm:example_block", combining the namespace and path
    public static final DeferredItem<BlockItem> EXAMPLE_BLOCK_ITEM = ITEMS.registerSimpleBlockItem("example_block", EXAMPLE_BLOCK);

    // Creates a new food item with the id "dingusprimeacm:example_id", nutrition 1 and saturation 2
    public static final DeferredItem<Item> EXAMPLE_ITEM = ITEMS.registerSimpleItem("example_item", new Item.Properties().food(new FoodProperties.Builder()
            .alwaysEdible().nutrition(1).saturationModifier(2f).build()));

    // Creates a creative tab with the id "dingusprimeacm:example_tab" for the example item, that is placed after the combat tab
    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> EXAMPLE_TAB = CREATIVE_MODE_TABS.register("example_tab", () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.dingusprimeacm")) //The language key for the title of your CreativeModeTab
            .withTabsBefore(CreativeModeTabs.COMBAT)
            .icon(() -> EXAMPLE_ITEM.get().getDefaultInstance())
            .displayItems((parameters, output) -> {
                output.accept(EXAMPLE_ITEM.get()); // Add the example item to the tab. For your own tabs, this method is preferred over the event
            }).build());


    public static final DeferredRegister<MenuType<?>> MENU_TYPE_DEFERRED_REGISTER = DeferredRegister.create(BuiltInRegistries.MENU, MODID);

    public static final Supplier<MenuType<GameboyInventoryMenu>> GAMEBOY_INVENTORY_MENU = MENU_TYPE_DEFERRED_REGISTER.register("gameboy_inventory_menu", () -> new MenuType<GameboyInventoryMenu>(GameboyInventoryMenu::new, FeatureFlags.DEFAULT_FLAGS));
    public static final Supplier<MenuType<GameboyMenu>> GAMEBOY_MENU = MENU_TYPE_DEFERRED_REGISTER.register("gameboy_menu", () -> new MenuType<GameboyMenu>(GameboyMenu::new, FeatureFlags.DEFAULT_FLAGS));


    public static final DeferredRegister.DataComponents DATA_COMPONENTS_REGISTRAR = DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, DingusPrimeAdditionalContentMod.MODID);

    public static final Supplier<DataComponentType<CartItemDataComponent>> CART_ITEM_DATA_COMPONENT = DATA_COMPONENTS_REGISTRAR.registerComponentType(
            "cart_item_data_component",
            builder -> builder
                    // The codec to read/write the data to disk
                    .persistent(CartItemDataComponent.CARTITEM_SAVE_CODEC)
                    // The codec to read/write the data across the network
                    .networkSynchronized(CartItemDataComponent.CARTITEM_STREAM_CODEC)
    );

    public void registerCapabilities(RegisterCapabilitiesEvent event) {
        for (Item item : BuiltInRegistries.ITEM) {
            if (item.getClass() == GameBoyItem.class) {
                event.registerItem(Capabilities.ItemHandler.ITEM,
                        (stack, ctx) -> new ComponentItemHandler(stack, DataComponents.CONTAINER, 1),
                        item
                );
            }
        }
    }


    // The constructor for the mod class is the first code that is run when your mod is loaded.
    // FML will recognize some parameter types like IEventBus or ModContainer and pass them in automatically.
    public DingusPrimeAdditionalContentMod(IEventBus modEventBus, ModContainer modContainer)
    {
        // Register the commonSetup method for modloading
        modEventBus.addListener(this::commonSetup);

        // Register the Deferred Register to the mod event bus so blocks get registered
        BLOCKS.register(modEventBus);
        // Register the Deferred Register to the mod event bus so items get registered
        ITEMS.register(modEventBus);
        // Register the Deferred Register to the mod event bus so tabs get registered
        CREATIVE_MODE_TABS.register(modEventBus);

        MENU_TYPE_DEFERRED_REGISTER.register(modEventBus);

        DATA_COMPONENTS_REGISTRAR.register(modEventBus);

        // Register ourselves for server and other game events we are interested in.
        // Note that this is necessary if and only if we want *this* class (DingusPrimeAdditionalContentMod) to respond directly to events.
        // Do not add this line if there are no @SubscribeEvent-annotated functions in this class, like onServerStarting() below.
        NeoForge.EVENT_BUS.register(this);

        modEventBus.addListener(this::registerCapabilities);

        // Register the DingusRegistries class to the mod event bus
        modEventBus.register(DingusRegistries.class);

        // Register the item to a creative tab
        modEventBus.addListener(this::addCreative);

        // Register our mod's ModConfigSpec so that FML can create and load the config file for us
        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);


        modEventBus.addListener(DataGeneration::generate);

    }

    private void commonSetup(final FMLCommonSetupEvent event)
    {
        // Some common setup code
        LOGGER.info("HELLO FROM COMMON SETUP");

        if (Config.logDirtBlock)
            LOGGER.info("DIRT BLOCK >> {}", BuiltInRegistries.BLOCK.getKey(Blocks.DIRT));

        //LOGGER.info(Config.magicNumberIntroduction + Config.magicNumber);

        //Config.items.forEach((item) -> LOGGER.info("ITEM >> {}", item.toString()));
    }

    // Add the example block item to the building blocks tab
    private void addCreative(BuildCreativeModeTabContentsEvent event)
    {
        if (event.getTabKey() == CreativeModeTabs.BUILDING_BLOCKS)
            event.accept(EXAMPLE_BLOCK_ITEM);
    }

    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event)
    {
        // Do something when the server starts
        LOGGER.info("HELLO from server starting");
        WorldDataManager.OnServerStarting(event);
    }

    @SubscribeEvent
    public void onServerStarted(ServerStartedEvent event) {
        WorldDataManager.OnServerStarted(event);
    }


    @EventBusSubscriber(modid = MODID, bus = EventBusSubscriber.Bus.MOD)
    public static class ModEvents
    {
        @SubscribeEvent
        public static void register(final RegisterPayloadHandlersEvent event) {
            // Sets the current network version
            final PayloadRegistrar registrar = event.registrar("1")
                    .executesOn(HandlerThread.NETWORK);
            PacketHandler.RegisterNetwork(registrar);
        }
    }


    // You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
    @EventBusSubscriber(modid = MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents
    {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event)
        {
            // Some client setup code
            LOGGER.info("HELLO FROM CLIENT SETUP");
            LOGGER.info("MINECRAFT NAME >> {}", Minecraft.getInstance().getUser().getName());
        }
    }



}
