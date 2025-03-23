//dingusprimeacm
package net.petercashel.dingusprimeacm;

import com.mojang.logging.LogUtils;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.neoforge.capabilities.BlockCapability;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.ItemCapability;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.common.util.TriState;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.event.entity.player.ItemEntityPickupEvent;
import net.neoforged.neoforge.event.server.ServerStartedEvent;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.event.lifecycle.InterModEnqueueEvent;
import net.neoforged.fml.event.lifecycle.InterModProcessEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.petercashel.dingusprimeacm.kubejs.types.cabnet.CabnetContainer;
import net.petercashel.dingusprimeacm.kubejs.types.cartshelf.container.CartShelfContainer;
import net.petercashel.dingusprimeacm.kubejs.types.chair.ChairEntity;
import net.petercashel.dingusprimeacm.commands.DingusPrimeAcmCommand;
import net.petercashel.dingusprimeacm.configuration.DPAcmConfig;
import net.petercashel.dingusprimeacm.kubejs.types.gameboy.container.GameboyCartContainer;
import net.petercashel.dingusprimeacm.kubejs.types.gameboy.container.GameboyContainer;
import net.petercashel.dingusprimeacm.kubejs.types.gameboy.capability.IGameBoyCartCapability;
import net.petercashel.dingusprimeacm.kubejs.types.gameboy.item.GameBoyCartItemJS;
import net.petercashel.dingusprimeacm.kubejs.types.gameboy.item.GameBoyCartItemJS.GameBoyCartCapabilityProvider;
import net.petercashel.dingusprimeacm.kubejs.types.gameboy.item.GameBoyItemJS;
import net.petercashel.dingusprimeacm.kubejs.types.gameboy.registry.RomInfo;
import net.petercashel.dingusprimeacm.kubejs.dingusprimeKubeJSPlugin;
import net.petercashel.dingusprimeacm.networking.PacketHandler;
import net.petercashel.dingusprimeacm.shopkeeper.container.ShopKeeperMenu;
import net.petercashel.dingusprimeacm.shopkeeper.entity.ShopKeeper;
import net.petercashel.dingusprimeacm.shopkeeper.entity.subtypes.*;
import net.petercashel.dingusprimeacm.shopkeeper.registry.ShopTradeInfo;
import net.petercashel.dingusprimeacm.shopkeeper.registry.ShopTradeManager;
import net.petercashel.dingusprimeacm.world.WorldDataManager;
import net.petercashel.dingusprimeacm.world.daily.DailyManager;
import org.slf4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod("dingusprimeacm")
public class dingusprimeacm
{
    public static final String MODID = "dingusprimeacm";
    // Directly reference a slf4j logger
    public static final Logger LOGGER = LogUtils.getLogger();


    private static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(BuiltInRegistries.BLOCK, MODID);
    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(BuiltInRegistries.ITEM, MODID);
    private static final DeferredRegister<MenuType<?>> CONTAINERS = DeferredRegister.create(BuiltInRegistries.MENU, MODID);
    public dingusprimeacm(IEventBus bus, Dist dist)
    {
        // Register the setup method for modloading
        bus.addListener(this::setup);
        // Register the enqueueIMC method for modloading
        bus.addListener(this::enqueueIMC);
        // Register the processIMC method for modloading
        bus.addListener(this::processIMC);

        // Register ourselves for server and other game events we are interested in
        NeoForge.EVENT_BUS.register(this);;
        NeoForge.EVENT_BUS.register(ForgeRegistryEvents.class);
        NeoForge.EVENT_BUS.register(DailyManager.DailyManagerEvents.class);
        bus.register(ModRegistryEvents.class);
        bus.addListener(this::registerCapabilities);

        CONTAINERS.register(bus);
        ENTITY_TYPES.register(bus);
        BLOCKS.register(bus);
        ITEMS.register(bus);

        DPAcmConfig.LoadConfig();
        DPAcmConfig.SaveConfig();
    }

    private void setup(final FMLCommonSetupEvent event)
    {
//        // some preinit code
//        LOGGER.info("HELLO FROM PREINIT");
//        LOGGER.info("DIRT BLOCK >> {}", Blocks.DIRT.getRegistryName());

        PacketHandler.RegisterNetwork(event);
    }

    private void enqueueIMC(final InterModEnqueueEvent event)
    {
        // Some example code to dispatch IMC to another mod
        //InterModComms.sendTo("examplemod", "helloworld", () -> { LOGGER.info("Hello world from the MDK"); return "Hello world";});
    }

    private void processIMC(final InterModProcessEvent event)
    {
        // Some example code to receive and process InterModComms from other mods
//        LOGGER.info("Got IMC {}", event.getIMCStream().
//                map(m->m.messageSupplier().get()).
//                collect(Collectors.toList()));
    }

    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event)
    {
        // Do something when the server starts
        ShopTradeManager.INSTANCE.ResetAll();
        WorldDataManager.OnServerStarting(event);
    }

    @SubscribeEvent
    public void onServerStarted(ServerStartedEvent event) {
        WorldDataManager.OnServerStarted(event);
    }

    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(BuiltInRegistries.ENTITY_TYPE, MODID);

    public static <T extends ShopKeeper> DeferredHolder<EntityType<?>, EntityType<T>> newShopKeeper(String name, EntityType.EntityFactory<T> factory) {
        return ENTITY_TYPES.register(name, () -> {
            return EntityType.Builder.<T>of(factory, MobCategory.AMBIENT)
                    .sized(1.0f, 2.0f).build(ResourceLocation.fromNamespaceAndPath(MODID, name).toString());
        });
    }

    public class ForgeRegistryEvents {
        @SubscribeEvent
        public static void registerCommands(RegisterCommandsEvent event){
            DingusPrimeAcmCommand.register(event.getDispatcher());
        }

    }

    public static final DPACM_MainTab DPACM_MAINTAB = new DPACM_MainTab(CreativeModeTab.TABS.length, MODID);


    public static class ModRegistryEvents
    {

        @SubscribeEvent
        public static void onBlocksRegistry(final RegisterEvent.Register<Block> blockRegistryEvent)
        {
            // Register a new block here

        }

        @SubscribeEvent
        public static void onRomsRegistry(final RegistryEvent.Register<RomInfo> rom)
        {
            dingusprimeKubeJSPlugin.RegistryEvent(rom);

        }

        @SubscribeEvent
        public static void onShopTradeRegistry(final RegistryEvent.Register<ShopTradeInfo> shopTradeInfo)
        {
            dingusprimeKubeJSPlugin.RegistryEventShopTrade(shopTradeInfo);

        }

        @SubscribeEvent
        public static void onItemsRegistry(final RegistryEvent.Register<Item> itemRegistryEvent)
        {
            // Register a new item here
            //itemRegistryEvent.getRegistry().register(new ForgeSpawnEggItem(SHOP_KEEPER, 0xffffff, 0x00ff00, new Item.Properties().tab(CreativeModeTab.TAB_MISC)).setRegistryName(MODID, "shopkeeperegg"));


        }

        @SubscribeEvent
        public static void onEntityAttributeCreation(final EntityAttributeCreationEvent entityAttributeCreationEvent) {

            entityAttributeCreationEvent.put(SHOP_KEEPER.get(), ShopKeeper.createAttributes().build());
            entityAttributeCreationEvent.put(SHOP_KEEPER_Furniture.get(), ShopKeeper.createAttributes().build());
            entityAttributeCreationEvent.put(SHOP_KEEPER_General.get(), ShopKeeper.createAttributes().build());

            entityAttributeCreationEvent.put(SHOP_KEEPER_weapons.get(), ShopKeeper.createAttributes().build());
            entityAttributeCreationEvent.put(SHOP_KEEPER_armor.get(), ShopKeeper.createAttributes().build());
            entityAttributeCreationEvent.put(SHOP_KEEPER_tools.get(), ShopKeeper.createAttributes().build());

            entityAttributeCreationEvent.put(SHOP_KEEPER_seeds.get(), ShopKeeper.createAttributes().build());
            entityAttributeCreationEvent.put(SHOP_KEEPER_trees.get(), ShopKeeper.createAttributes().build());
            entityAttributeCreationEvent.put(SHOP_KEEPER_plants.get(), ShopKeeper.createAttributes().build());

            entityAttributeCreationEvent.put(SHOP_KEEPER_cosmetic.get(), ShopKeeper.createAttributes().build());
            entityAttributeCreationEvent.put(SHOP_KEEPER_hats.get(), ShopKeeper.createAttributes().build());
            entityAttributeCreationEvent.put(SHOP_KEEPER_shirts.get(), ShopKeeper.createAttributes().build());
            entityAttributeCreationEvent.put(SHOP_KEEPER_pants.get(), ShopKeeper.createAttributes().build());
            entityAttributeCreationEvent.put(SHOP_KEEPER_shoes.get(), ShopKeeper.createAttributes().build());

            entityAttributeCreationEvent.put(SHOP_KEEPER_curios.get(), ShopKeeper.createAttributes().build());

            entityAttributeCreationEvent.put(SHOP_KEEPER_custom1.get(), ShopKeeper.createAttributes().build());
            entityAttributeCreationEvent.put(SHOP_KEEPER_custom2.get(), ShopKeeper.createAttributes().build());
            entityAttributeCreationEvent.put(SHOP_KEEPER_custom3.get(), ShopKeeper.createAttributes().build());
            entityAttributeCreationEvent.put(SHOP_KEEPER_custom4.get(), ShopKeeper.createAttributes().build());

        }
    }

    public static final DeferredHolder<EntityType<?>, EntityType<ChairEntity>> CHAIR_ENTITY_TYPE = ENTITY_TYPES.register("entity_chair", () -> {
        return EntityType.Builder.<ChairEntity>of(ChairEntity::new, MobCategory.MISC)
                .setTrackingRange(256)
                .setUpdateInterval(20)
                .sized(0.0001F, 0.0001F)
                .build("entity_chair");
    });


    public static final DeferredHolder<EntityType<?>, EntityType<ShopKeeper>> SHOP_KEEPER = newShopKeeper("shopkeeper", ShopKeeper::new);
//            ENTITY_TYPES.register("shopkeeper", () -> {
//                return EntityType.Builder.<ShopKeeper>of(ShopKeeper::new, MobCategory.AMBIENT)
//                        .sized(1.0f, 2.0f).build(new ResourceLocation(MODID, "shopkeeper").toString());
//            });

    public static final DeferredHolder<EntityType<?>, EntityType<ShopKeeper_Furniture>> SHOP_KEEPER_Furniture = newShopKeeper("shopkeeper_furniture", ShopKeeper_Furniture::new);
    public static final DeferredHolder<EntityType<?>, EntityType<ShopKeeper_General>> SHOP_KEEPER_General = newShopKeeper("shopkeeper_general", ShopKeeper_General::new);

    public static final DeferredHolder<EntityType<?>, EntityType<ShopKeeper_weapons>> SHOP_KEEPER_weapons = newShopKeeper("shopkeeper_weapons", ShopKeeper_weapons::new);
    public static final DeferredHolder<EntityType<?>, EntityType<ShopKeeper_armor>> SHOP_KEEPER_armor = newShopKeeper("shopkeeper_armor", ShopKeeper_armor::new);
    public static final DeferredHolder<EntityType<?>, EntityType<ShopKeeper_tools>> SHOP_KEEPER_tools = newShopKeeper("shopkeeper_tools", ShopKeeper_tools::new);

    public static final DeferredHolder<EntityType<?>, EntityType<ShopKeeper_seeds>> SHOP_KEEPER_seeds = newShopKeeper("shopkeeper_seeds", ShopKeeper_seeds::new);
    public static final DeferredHolder<EntityType<?>, EntityType<ShopKeeper_trees>> SHOP_KEEPER_trees = newShopKeeper("shopkeeper_trees", ShopKeeper_trees::new);
    public static final DeferredHolder<EntityType<?>, EntityType<ShopKeeper_plants>> SHOP_KEEPER_plants = newShopKeeper("shopkeeper_plants", ShopKeeper_plants::new);

    public static final DeferredHolder<EntityType<?>, EntityType<ShopKeeper_cosmetic>> SHOP_KEEPER_cosmetic = newShopKeeper("shopkeeper_cosmetic", ShopKeeper_cosmetic::new);
    public static final DeferredHolder<EntityType<?>, EntityType<ShopKeeper_hats>> SHOP_KEEPER_hats = newShopKeeper("shopkeeper_hats", ShopKeeper_hats::new);
    public static final DeferredHolder<EntityType<?>, EntityType<ShopKeeper_shirts>> SHOP_KEEPER_shirts = newShopKeeper("shopkeeper_shirts", ShopKeeper_shirts::new);
    public static final DeferredHolder<EntityType<?>, EntityType<ShopKeeper_pants>> SHOP_KEEPER_pants = newShopKeeper("shopkeeper_pants", ShopKeeper_pants::new);
    public static final DeferredHolder<EntityType<?>, EntityType<ShopKeeper_shoes>> SHOP_KEEPER_shoes = newShopKeeper("shopkeeper_shoes", ShopKeeper_shoes::new);

    public static final DeferredHolder<EntityType<?>, EntityType<ShopKeeper_curios>> SHOP_KEEPER_curios = newShopKeeper("shopkeeper_curios", ShopKeeper_curios::new);

    public static final DeferredHolder<EntityType<?>, EntityType<ShopKeeper_custom1>> SHOP_KEEPER_custom1 = newShopKeeper("shopkeeper_custom1", ShopKeeper_custom1::new);
    public static final DeferredHolder<EntityType<?>, EntityType<ShopKeeper_custom2>> SHOP_KEEPER_custom2 = newShopKeeper("shopkeeper_custom2", ShopKeeper_custom2::new);
    public static final DeferredHolder<EntityType<?>, EntityType<ShopKeeper_custom3>> SHOP_KEEPER_custom3 = newShopKeeper("shopkeeper_custom3", ShopKeeper_custom3::new);
    public static final DeferredHolder<EntityType<?>, EntityType<ShopKeeper_custom4>> SHOP_KEEPER_custom4 = newShopKeeper("shopkeeper_custom4", ShopKeeper_custom4::new);

    public static final ItemCapability<GameBoyCartCapabilityProvider, Void> GAMEBOYCART_CAP_INSTANCE =
            ItemCapability.create(
                    // Provide a name to uniquely identify the capability.
                    ResourceLocation.fromNamespaceAndPath("dingusprimeacm", "gameboycart_capability"),
                    // Provide the queried type. Here, we want to look up `IItemHandler` instances.
                    GameBoyCartCapabilityProvider.class,
                    Void.class);

    private void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerItem(
                Capabilities.ItemHandler.ITEM, // capability to register for
                (itemStack, context) -> {
                    return new GameBoyCartCapabilityProvider();
                },
            // items to register for
                UNKOWN_ITEM // default value

        );
    }


    public static final DeferredHolder<MenuType<?>, MenuType<ShopKeeperMenu>> SHOP_KEEPER_CONTAINER = CONTAINERS.register("shopkeepermenu",
            () -> IMenuTypeExtension.create((windowId, inv, data) -> new ShopKeeperMenu(windowId, inv)));

    public static final DeferredHolder<MenuType<?>, MenuType<GameboyContainer>> GAMEBOY_CONTAINER = CONTAINERS.register("gameboy",
            () -> IMenuTypeExtension.create((windowId, inv, data) -> new GameboyContainer(windowId, inv, inv.player, inv.player.getItemInHand(inv.player.getUsedItemHand()))));

    public static final DeferredHolder<MenuType<?>, MenuType<GameboyCartContainer>> GAMEBOYCART_CONTAINER = CONTAINERS.register("gameboycart",
            () -> IMenuTypeExtension.create((windowId, inv, data) -> new GameboyCartContainer(windowId, inv, inv.player, inv.player.getItemInHand(inv.player.getUsedItemHand()))));

    public static final DeferredHolder<MenuType<?>, MenuType<CartShelfContainer>> CARTSHELF_CONTAINER = CONTAINERS.register("cartshelf",
            () -> IMenuTypeExtension.create((windowId, inv, data) -> new CartShelfContainer(windowId, data.readBlockPos(), inv, inv.player)));

    public static final DeferredHolder<MenuType<?>, MenuType<CabnetContainer>> CABNET_CONTAINER = CONTAINERS.register("cabnet",
            () -> IMenuTypeExtension.create((windowId, inv, data) -> new CabnetContainer(windowId, data.readBlockPos(), inv, inv.player)));


    @SubscribeEvent
    public void pickupItem(ItemEntityPickupEvent.Pre event) {
        //Bug work around
        if (event.getItemEntity().getItem().getItem() instanceof GameBoyItemJS && event.getPlayer().isCreative()) {
            event.setCanPickup(TriState.FALSE);
        }
    }

}
