package net.petercashel.dingusprimeacm;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.client.settings.KeyConflictContext;
import net.neoforged.neoforge.common.util.Lazy;
import net.petercashel.dingusprimeacm.kubejs.types.gameboy.client.GameboyInventoryMenuScreen;
import net.petercashel.dingusprimeacm.kubejs.types.gameboy.client.GameboyMenuScreen;
import net.petercashel.dingusprimeacm.kubejs.types.gameboy.client.emulation.GameboyAudio;
import net.petercashel.dingusprimeacm.kubejs.types.gameboy.client.emulation.GameboyController;
import net.petercashel.dingusprimeacm.kubejs.types.gameboy.client.emulation.GameboyDisplay;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;


@EventBusSubscriber(modid = DingusPrimeAdditionalContentMod.MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class DingusPrimeAdditionalContentMod_Client {

    public static final Lazy<KeyMapping> GB_A = Lazy.of(() -> WithGUIConflictContext(new KeyMapping("key.GB_A", GLFW.GLFW_KEY_X, "key.categories." + DingusPrimeAdditionalContentMod.MODID)));
    public static final Lazy<KeyMapping> GB_B = Lazy.of(() -> WithGUIConflictContext(new KeyMapping("key.GB_B", GLFW.GLFW_KEY_Z, "key.categories." + DingusPrimeAdditionalContentMod.MODID)));
    public static final Lazy<KeyMapping> GB_UP = Lazy.of(() -> WithGUIConflictContext(new KeyMapping("key.GB_UP", GLFW.GLFW_KEY_UP, "key.categories." + DingusPrimeAdditionalContentMod.MODID)));
    public static final Lazy<KeyMapping> GB_DOWN = Lazy.of(() -> WithGUIConflictContext(new KeyMapping("key.GB_DOWN", GLFW.GLFW_KEY_DOWN, "key.categories." + DingusPrimeAdditionalContentMod.MODID)));
    public static final Lazy<KeyMapping> GB_LEFT = Lazy.of(() -> WithGUIConflictContext(new KeyMapping("key.GB_LEFT", GLFW.GLFW_KEY_LEFT, "key.categories." + DingusPrimeAdditionalContentMod.MODID)));
    public static final Lazy<KeyMapping> GB_RIGHT = Lazy.of(() -> WithGUIConflictContext(new KeyMapping("key.GB_RIGHT", GLFW.GLFW_KEY_RIGHT, "key.categories." + DingusPrimeAdditionalContentMod.MODID)));
    public static final Lazy<KeyMapping> GB_SEL = Lazy.of(() -> WithGUIConflictContext(new KeyMapping("key.GB_SEL", GLFW.GLFW_KEY_RIGHT_SHIFT, "key.categories." + DingusPrimeAdditionalContentMod.MODID)));
    public static final Lazy<KeyMapping> GB_START = Lazy.of(() -> WithGUIConflictContext(new KeyMapping("key.GB_START", GLFW.GLFW_KEY_ENTER, "key.categories." + DingusPrimeAdditionalContentMod.MODID)));


    // Event is on the mod event bus only on the physical client
    @SubscribeEvent
    public static void registerBindings(RegisterKeyMappingsEvent event) {
        event.register(AddLazyToList(GB_A    ).get());
        event.register(AddLazyToList(GB_B    ).get());
        event.register(AddLazyToList(GB_UP   ).get());
        event.register(AddLazyToList(GB_DOWN ).get());
        event.register(AddLazyToList(GB_LEFT ).get());
        event.register(AddLazyToList(GB_RIGHT).get());
        event.register(AddLazyToList(GB_SEL  ).get());
        event.register(AddLazyToList(GB_START).get());
    }

    public static Lazy<KeyMapping> AddLazyToList(Lazy<KeyMapping> mapping) {
        if (GBBinds == null) {
            GBBinds = new ArrayList<Lazy<KeyMapping>>();
        }
        GBBinds.add(mapping);
        return mapping;
    }

    public static KeyMapping WithGUIConflictContext(KeyMapping mapping) {
        mapping.setKeyConflictContext(KeyConflictContext.GUI);
        return mapping;
    }

    @SubscribeEvent
    // Event is listened to on the mod event bus
    public static void registerScreens(RegisterMenuScreensEvent event) {
        event.register(DingusPrimeAdditionalContentMod.GAMEBOY_INVENTORY_MENU.get(), GameboyInventoryMenuScreen::new);
        event.register(DingusPrimeAdditionalContentMod.GAMEBOY_MENU.get(), GameboyMenuScreen::new);
    }

    @SubscribeEvent
    public static void onClientSetupEvent(final FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
//            MenuScreens.register(DingusPrimeAdditionalContentMod.GAMEBOY_CONTAINER.get(), GameboyScreen::new);
//            MenuScreens.register(DingusPrimeAdditionalContentMod.GAMEBOYCART_CONTAINER.get(), GameboyCartScreen::new);
//            MenuScreens.register(DingusPrimeAdditionalContentMod.SHOP_KEEPER_CONTAINER.get(), ShopKeeperScreen::new);
//            MenuScreens.register(DingusPrimeAdditionalContentMod.CARTSHELF_CONTAINER.get(), CartShelfScreen::new);
//            MenuScreens.register(DingusPrimeAdditionalContentMod.CABNET_CONTAINER.get(), CabnetScreen::new);


            if (GBBinds == null) {
                GBBinds = new ArrayList<Lazy<KeyMapping>>();
            }
//
//            EntityRenderers.register(DingusPrimeAdditionalContentMod.SHOP_KEEPER.get(), ShopKeeperRenderer::new);
//            EntityRenderers.register(DingusPrimeAdditionalContentMod.SHOP_KEEPER_Furniture.get(), ShopKeeperRenderer::new);
//            EntityRenderers.register(DingusPrimeAdditionalContentMod.SHOP_KEEPER_General.get(), ShopKeeperRenderer::new);
//
//            EntityRenderers.register(DingusPrimeAdditionalContentMod.SHOP_KEEPER_weapons.get(), ShopKeeperRenderer::new);
//            EntityRenderers.register(DingusPrimeAdditionalContentMod.SHOP_KEEPER_armor.get(), ShopKeeperRenderer::new);
//            EntityRenderers.register(DingusPrimeAdditionalContentMod.SHOP_KEEPER_tools.get(), ShopKeeperRenderer::new);
//
//            EntityRenderers.register(DingusPrimeAdditionalContentMod.SHOP_KEEPER_seeds.get(), ShopKeeperRenderer::new);
//            EntityRenderers.register(DingusPrimeAdditionalContentMod.SHOP_KEEPER_trees.get(), ShopKeeperRenderer::new);
//            EntityRenderers.register(DingusPrimeAdditionalContentMod.SHOP_KEEPER_plants.get(), ShopKeeperRenderer::new);
//
//            EntityRenderers.register(DingusPrimeAdditionalContentMod.SHOP_KEEPER_cosmetic.get(), ShopKeeperRenderer::new);
//            EntityRenderers.register(DingusPrimeAdditionalContentMod.SHOP_KEEPER_hats.get(), ShopKeeperRenderer::new);
//            EntityRenderers.register(DingusPrimeAdditionalContentMod.SHOP_KEEPER_shirts.get(), ShopKeeperRenderer::new);
//            EntityRenderers.register(DingusPrimeAdditionalContentMod.SHOP_KEEPER_pants.get(), ShopKeeperRenderer::new);
//            EntityRenderers.register(DingusPrimeAdditionalContentMod.SHOP_KEEPER_shoes.get(), ShopKeeperRenderer::new);
//
//            EntityRenderers.register(DingusPrimeAdditionalContentMod.SHOP_KEEPER_curios.get(), ShopKeeperRenderer::new);
//
//            EntityRenderers.register(DingusPrimeAdditionalContentMod.SHOP_KEEPER_custom1.get(), ShopKeeperRenderer::new);
//            EntityRenderers.register(DingusPrimeAdditionalContentMod.SHOP_KEEPER_custom2.get(), ShopKeeperRenderer::new);
//            EntityRenderers.register(DingusPrimeAdditionalContentMod.SHOP_KEEPER_custom3.get(), ShopKeeperRenderer::new);
//            EntityRenderers.register(DingusPrimeAdditionalContentMod.SHOP_KEEPER_custom4.get(), ShopKeeperRenderer::new);
//
//
//            EntityRenderers.register(DingusPrimeAdditionalContentMod.CHAIR_ENTITY_TYPE.get(), EmptyRenderer::new);
        });

    }

//    private static class EmptyRenderer extends EntityRenderer<ChairEntity>
//    {
//        protected EmptyRenderer(EntityRendererProvider.Context ctx)
//        {
//            super(ctx);
//        }
//
//        @Override
//        public boolean shouldRender(ChairEntity entity, Frustum camera, double camX, double camY, double camZ)
//        {
//            return false;
//        }
//
//        @Override
//        public ResourceLocation getTextureLocation(ChairEntity entity)
//        {
//            return null;
//        }
//    }

    @SubscribeEvent
    public static void onEntityRenderersEvent(final EntityRenderersEvent.RegisterRenderers event)
    {
//        if (dingusprimeKubeJSPlugin.CARTSHELF_BE != null) {
//            event.registerBlockEntityRenderer(dingusprimeKubeJSPlugin.CARTSHELF_BE, new BlockEntityRendererProvider() {
//                @Override
//                public BlockEntityRenderer create(Context pContext) {
//                    return new CartShelfBlockEntityRenderer(pContext);
//                }
//            });
//        }
    }

    @EventBusSubscriber(modid = DingusPrimeAdditionalContentMod.MODID, bus = EventBusSubscriber.Bus.GAME, value = Dist.CLIENT)
    public class acmClientEvents_forge {

        @SubscribeEvent
        public static void onClientDisconnectionFromServer(net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent.LoggingOut event) {
            try {
                GameboyMenuScreen.lastInstance.emulator.StopEmulation();
            } catch (Exception ex) {
                //I dont care
            }
        }

    }

    private static ArrayList<Lazy<KeyMapping>> GBBinds;
    public static GameboyController controller = new GameboyController();
    public static GameboyDisplay gameboyDisplay = new GameboyDisplay();
    public static GameboyAudio gameboyAudio = new GameboyAudio();


    public static boolean HandlePressed(int pKeyCode, int pScanCode, int pModifiers) {
        if (GBBinds != null) { //SAFETY
            for (int i = 0; i < GBBinds.size(); i++) {
                KeyMapping mapping = GBBinds.get(i).get();
                if (mapping.isActiveAndMatches(InputConstants.getKey(pKeyCode, pScanCode))) {
                    controller.UpdateBinding(mapping, true);
                    return true;
                }
            }
        }
        return false;
    }
    public static boolean HandleReleased(int pKeyCode, int pScanCode, int pModifiers) {
        if (GBBinds != null) { //SAFETY
            for (int i = 0; i < GBBinds.size(); i++) {
                KeyMapping mapping = GBBinds.get(i).get();
                if (mapping.isActiveAndMatches(InputConstants.getKey(pKeyCode, pScanCode))) {
                    controller.UpdateBinding(mapping, false);
                    return true;
                }
            }
        }
        return false;
    }
}


