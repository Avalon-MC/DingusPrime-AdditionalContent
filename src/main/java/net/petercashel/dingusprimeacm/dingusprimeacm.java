//dingusprimeacm
package net.petercashel.dingusprimeacm;

import com.mojang.logging.LogUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.ObjectHolder;
import net.minecraftforge.registries.RegistryObject;
import net.petercashel.dingusprimeacm.cabnet.CabnetContainer;
import net.petercashel.dingusprimeacm.cartshelf.block.CartShelfBlockEntity;
import net.petercashel.dingusprimeacm.cartshelf.container.CartShelfContainer;
import net.petercashel.dingusprimeacm.chair.ChairEntity;
import net.petercashel.dingusprimeacm.gameboy.container.GameboyCartContainer;
import net.petercashel.dingusprimeacm.gameboy.container.GameboyContainer;
import net.petercashel.dingusprimeacm.gameboy.capability.IGameBoyCartCapability;
import net.petercashel.dingusprimeacm.gameboy.item.GameBoyItemJS;
import net.petercashel.dingusprimeacm.gameboy.registry.RomInfo;
import net.petercashel.dingusprimeacm.kubejs.dingusprimeKubeJSPlugin;
import net.petercashel.dingusprimeacm.networking.PacketHandler;
import net.petercashel.dingusprimeacm.shopkeeper.container.ShopKeeperMenu;
import net.petercashel.dingusprimeacm.shopkeeper.entity.ShopKeeper;
import net.petercashel.dingusprimeacm.shopkeeper.entity.subtypes.*;
import net.petercashel.dingusprimeacm.shopkeeper.registry.ShopTradeInfo;
import net.petercashel.dingusprimeacm.shopkeeper.registry.ShopTradeManager;
import org.slf4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod("dingusprimeacm")
public class dingusprimeacm
{
    public static final String MODID = "dingusprimeacm";
    // Directly reference a slf4j logger
    private static final Logger LOGGER = LogUtils.getLogger();



    private static final DeferredRegister<MenuType<?>> CONTAINERS = DeferredRegister.create(ForgeRegistries.CONTAINERS, MODID);
    public dingusprimeacm()
    {
        // Register the setup method for modloading
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        // Register the enqueueIMC method for modloading
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::enqueueIMC);
        // Register the processIMC method for modloading
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::processIMC);

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);

        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        CONTAINERS.register(bus);
        ENTITY_TYPES.register(bus);
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

    }

    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.ENTITIES, MODID);

    public static <T extends ShopKeeper> RegistryObject<EntityType<T>> newShopKeeper(String name, EntityType.EntityFactory<T> factory) {
        return ENTITY_TYPES.register(name, () -> {
            return EntityType.Builder.<T>of(factory, MobCategory.AMBIENT)
                    .sized(1.0f, 2.0f).build(new ResourceLocation(MODID, name).toString());
        });
    }



    // You can use EventBusSubscriber to automatically subscribe events on the contained class (this is subscribing to the MOD
    // Event bus for receiving Registry Events)
    @Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class RegistryEvents
    {

        @SubscribeEvent
        public static void onBlocksRegistry(final RegistryEvent.Register<Block> blockRegistryEvent)
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

    public static final RegistryObject<EntityType<ChairEntity>> CHAIR_ENTITY_TYPE = ENTITY_TYPES.register("entity_chair", () -> {
        return EntityType.Builder.<ChairEntity>of(ChairEntity::new, MobCategory.MISC)
                .setTrackingRange(256)
                .setUpdateInterval(20)
                .sized(0.0001F, 0.0001F)
                .build("entity_chair");
    });


    public static final RegistryObject<EntityType<ShopKeeper>> SHOP_KEEPER = newShopKeeper("shopkeeper", ShopKeeper::new);
//            ENTITY_TYPES.register("shopkeeper", () -> {
//                return EntityType.Builder.<ShopKeeper>of(ShopKeeper::new, MobCategory.AMBIENT)
//                        .sized(1.0f, 2.0f).build(new ResourceLocation(MODID, "shopkeeper").toString());
//            });

    public static final RegistryObject<EntityType<ShopKeeper_Furniture>> SHOP_KEEPER_Furniture = newShopKeeper("shopkeeper_furniture", ShopKeeper_Furniture::new);
    public static final RegistryObject<EntityType<ShopKeeper_General>> SHOP_KEEPER_General = newShopKeeper("shopkeeper_general", ShopKeeper_General::new);

    public static final RegistryObject<EntityType<ShopKeeper_weapons>> SHOP_KEEPER_weapons = newShopKeeper("shopkeeper_weapons", ShopKeeper_weapons::new);
    public static final RegistryObject<EntityType<ShopKeeper_armor>> SHOP_KEEPER_armor = newShopKeeper("shopkeeper_armor", ShopKeeper_armor::new);
    public static final RegistryObject<EntityType<ShopKeeper_tools>> SHOP_KEEPER_tools = newShopKeeper("shopkeeper_tools", ShopKeeper_tools::new);

    public static final RegistryObject<EntityType<ShopKeeper_seeds>> SHOP_KEEPER_seeds = newShopKeeper("shopkeeper_seeds", ShopKeeper_seeds::new);
    public static final RegistryObject<EntityType<ShopKeeper_trees>> SHOP_KEEPER_trees = newShopKeeper("shopkeeper_trees", ShopKeeper_trees::new);
    public static final RegistryObject<EntityType<ShopKeeper_plants>> SHOP_KEEPER_plants = newShopKeeper("shopkeeper_plants", ShopKeeper_plants::new);

    public static final RegistryObject<EntityType<ShopKeeper_cosmetic>> SHOP_KEEPER_cosmetic = newShopKeeper("shopkeeper_cosmetic", ShopKeeper_cosmetic::new);
    public static final RegistryObject<EntityType<ShopKeeper_hats>> SHOP_KEEPER_hats = newShopKeeper("shopkeeper_hats", ShopKeeper_hats::new);
    public static final RegistryObject<EntityType<ShopKeeper_shirts>> SHOP_KEEPER_shirts = newShopKeeper("shopkeeper_shirts", ShopKeeper_shirts::new);
    public static final RegistryObject<EntityType<ShopKeeper_pants>> SHOP_KEEPER_pants = newShopKeeper("shopkeeper_pants", ShopKeeper_pants::new);
    public static final RegistryObject<EntityType<ShopKeeper_shoes>> SHOP_KEEPER_shoes = newShopKeeper("shopkeeper_shoes", ShopKeeper_shoes::new);

    public static final RegistryObject<EntityType<ShopKeeper_curios>> SHOP_KEEPER_curios = newShopKeeper("shopkeeper_curios", ShopKeeper_curios::new);

    public static final RegistryObject<EntityType<ShopKeeper_custom1>> SHOP_KEEPER_custom1 = newShopKeeper("shopkeeper_custom1", ShopKeeper_custom1::new);
    public static final RegistryObject<EntityType<ShopKeeper_custom2>> SHOP_KEEPER_custom2 = newShopKeeper("shopkeeper_custom2", ShopKeeper_custom2::new);
    public static final RegistryObject<EntityType<ShopKeeper_custom3>> SHOP_KEEPER_custom3 = newShopKeeper("shopkeeper_custom3", ShopKeeper_custom3::new);
    public static final RegistryObject<EntityType<ShopKeeper_custom4>> SHOP_KEEPER_custom4 = newShopKeeper("shopkeeper_custom4", ShopKeeper_custom4::new);




    public static final Capability<IGameBoyCartCapability> GAMEBOYCART_CAP_INSTANCE = CapabilityManager.get(new CapabilityToken<>() {});

    public static final RegistryObject<MenuType<ShopKeeperMenu>> SHOP_KEEPER_CONTAINER = CONTAINERS.register("shopkeepermenu",
            () -> IForgeMenuType.create((windowId, inv, data) -> new ShopKeeperMenu(windowId, inv)));

    public static final RegistryObject<MenuType<GameboyContainer>> GAMEBOY_CONTAINER = CONTAINERS.register("gameboy",
            () -> IForgeMenuType.create((windowId, inv, data) -> new GameboyContainer(windowId, inv, inv.player, inv.player.getItemInHand(inv.player.getUsedItemHand()))));

    public static final RegistryObject<MenuType<GameboyCartContainer>> GAMEBOYCART_CONTAINER = CONTAINERS.register("gameboycart",
            () -> IForgeMenuType.create((windowId, inv, data) -> new GameboyCartContainer(windowId, inv, inv.player, inv.player.getItemInHand(inv.player.getUsedItemHand()))));

    public static final RegistryObject<MenuType<CartShelfContainer>> CARTSHELF_CONTAINER = CONTAINERS.register("cartshelf",
            () -> IForgeMenuType.create((windowId, inv, data) -> new CartShelfContainer(windowId, data.readBlockPos(), inv, inv.player)));

    public static final RegistryObject<MenuType<CabnetContainer>> CABNET_CONTAINER = CONTAINERS.register("cabnet",
            () -> IForgeMenuType.create((windowId, inv, data) -> new CabnetContainer(windowId, data.readBlockPos(), inv, inv.player)));


    @SubscribeEvent
    public void pickupItem(EntityItemPickupEvent event) {
        //Bug work around
        if (event.getItem().getItem().getItem() instanceof GameBoyItemJS && event.getPlayer().isCreative()) {
            event.setCanceled(true);
        }
    }

}
