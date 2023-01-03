package net.petercashel.dingusprimeacm;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.ClientRegistry;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.petercashel.dingusprimeacm.kubejs.types.cabnet.client.CabnetScreen;
import net.petercashel.dingusprimeacm.kubejs.types.cartshelf.client.CartShelfScreen;
import net.petercashel.dingusprimeacm.kubejs.types.chair.ChairEntity;
import net.petercashel.dingusprimeacm.kubejs.types.gameboy.client.emulation.GameboyAudio;
import net.petercashel.dingusprimeacm.kubejs.types.gameboy.client.emulation.GameboyCartScreen;
import net.petercashel.dingusprimeacm.kubejs.types.gameboy.client.emulation.GameboyController;
import net.petercashel.dingusprimeacm.kubejs.types.gameboy.client.emulation.GameboyDisplay;
import net.petercashel.dingusprimeacm.kubejs.types.gameboy.client.GameboyScreen;
import net.petercashel.dingusprimeacm.kubejs.types.cartshelf.client.CartShelfBlockEntityRenderer;
import net.petercashel.dingusprimeacm.kubejs.dingusprimeKubeJSPlugin;
import net.petercashel.dingusprimeacm.shopkeeper.container.ShopKeeperScreen;
import net.petercashel.dingusprimeacm.shopkeeper.entity.client.ShopKeeperRenderer;
import org.lwjgl.glfw.GLFW;


@Mod.EventBusSubscriber(modid = dingusprimeacm.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class dingusprimeacm_client {

    public static final KeyMapping GB_A = new KeyMapping("key.GB_A", GLFW.GLFW_KEY_X, "key.categories." + dingusprimeacm.MODID);
    public static final KeyMapping GB_B = new KeyMapping("key.GB_B", GLFW.GLFW_KEY_Z, "key.categories." + dingusprimeacm.MODID);
    public static final KeyMapping GB_UP = new KeyMapping("key.GB_UP", GLFW.GLFW_KEY_UP, "key.categories." + dingusprimeacm.MODID);
    public static final KeyMapping GB_DOWN = new KeyMapping("key.GB_DOWN", GLFW.GLFW_KEY_DOWN, "key.categories." + dingusprimeacm.MODID);
    public static final KeyMapping GB_LEFT = new KeyMapping("key.GB_LEFT", GLFW.GLFW_KEY_LEFT, "key.categories." + dingusprimeacm.MODID);
    public static final KeyMapping GB_RIGHT = new KeyMapping("key.GB_RIGHT", GLFW.GLFW_KEY_RIGHT, "key.categories." + dingusprimeacm.MODID);
    public static final KeyMapping GB_SEL = new KeyMapping("key.GB_SEL", GLFW.GLFW_KEY_RIGHT_SHIFT, "key.categories." + dingusprimeacm.MODID);
    public static final KeyMapping GB_START = new KeyMapping("key.GB_START", GLFW.GLFW_KEY_ENTER, "key.categories." + dingusprimeacm.MODID);

    @SubscribeEvent
    public static void onClientSetupEvent(final FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            MenuScreens.register(dingusprimeacm.GAMEBOY_CONTAINER.get(), GameboyScreen::new);
            MenuScreens.register(dingusprimeacm.GAMEBOYCART_CONTAINER.get(), GameboyCartScreen::new);
            MenuScreens.register(dingusprimeacm.SHOP_KEEPER_CONTAINER.get(), ShopKeeperScreen::new);
            MenuScreens.register(dingusprimeacm.CARTSHELF_CONTAINER.get(), CartShelfScreen::new);
            MenuScreens.register(dingusprimeacm.CABNET_CONTAINER.get(), CabnetScreen::new);

            GB_A.setKeyConflictContext(KeyConflictContext.GUI);
            GB_B.setKeyConflictContext(KeyConflictContext.GUI);
            GB_UP.setKeyConflictContext(KeyConflictContext.GUI);
            GB_DOWN.setKeyConflictContext(KeyConflictContext.GUI);
            GB_LEFT.setKeyConflictContext(KeyConflictContext.GUI);
            GB_RIGHT.setKeyConflictContext(KeyConflictContext.GUI);
            GB_SEL.setKeyConflictContext(KeyConflictContext.GUI);
            GB_START.setKeyConflictContext(KeyConflictContext.GUI);

            ClientRegistry.registerKeyBinding(GB_A);
            ClientRegistry.registerKeyBinding(GB_B);
            ClientRegistry.registerKeyBinding(GB_UP);
            ClientRegistry.registerKeyBinding(GB_DOWN);
            ClientRegistry.registerKeyBinding(GB_LEFT);
            ClientRegistry.registerKeyBinding(GB_RIGHT);
            ClientRegistry.registerKeyBinding(GB_SEL);
            ClientRegistry.registerKeyBinding(GB_START);

            GBBinds = new KeyMapping[]{GB_A, GB_B, GB_SEL, GB_START, GB_UP, GB_DOWN, GB_LEFT, GB_RIGHT};


            EntityRenderers.register(dingusprimeacm.SHOP_KEEPER.get(), ShopKeeperRenderer::new);
            EntityRenderers.register(dingusprimeacm.SHOP_KEEPER_Furniture.get(), ShopKeeperRenderer::new);
            EntityRenderers.register(dingusprimeacm.SHOP_KEEPER_General.get(), ShopKeeperRenderer::new);

            EntityRenderers.register(dingusprimeacm.SHOP_KEEPER_weapons.get(), ShopKeeperRenderer::new);
            EntityRenderers.register(dingusprimeacm.SHOP_KEEPER_armor.get(), ShopKeeperRenderer::new);
            EntityRenderers.register(dingusprimeacm.SHOP_KEEPER_tools.get(), ShopKeeperRenderer::new);

            EntityRenderers.register(dingusprimeacm.SHOP_KEEPER_seeds.get(), ShopKeeperRenderer::new);
            EntityRenderers.register(dingusprimeacm.SHOP_KEEPER_trees.get(), ShopKeeperRenderer::new);
            EntityRenderers.register(dingusprimeacm.SHOP_KEEPER_plants.get(), ShopKeeperRenderer::new);

            EntityRenderers.register(dingusprimeacm.SHOP_KEEPER_cosmetic.get(), ShopKeeperRenderer::new);
            EntityRenderers.register(dingusprimeacm.SHOP_KEEPER_hats.get(), ShopKeeperRenderer::new);
            EntityRenderers.register(dingusprimeacm.SHOP_KEEPER_shirts.get(), ShopKeeperRenderer::new);
            EntityRenderers.register(dingusprimeacm.SHOP_KEEPER_pants.get(), ShopKeeperRenderer::new);
            EntityRenderers.register(dingusprimeacm.SHOP_KEEPER_shoes.get(), ShopKeeperRenderer::new);

            EntityRenderers.register(dingusprimeacm.SHOP_KEEPER_curios.get(), ShopKeeperRenderer::new);

            EntityRenderers.register(dingusprimeacm.SHOP_KEEPER_custom1.get(), ShopKeeperRenderer::new);
            EntityRenderers.register(dingusprimeacm.SHOP_KEEPER_custom2.get(), ShopKeeperRenderer::new);
            EntityRenderers.register(dingusprimeacm.SHOP_KEEPER_custom3.get(), ShopKeeperRenderer::new);
            EntityRenderers.register(dingusprimeacm.SHOP_KEEPER_custom4.get(), ShopKeeperRenderer::new);


            EntityRenderers.register(dingusprimeacm.CHAIR_ENTITY_TYPE.get(), EmptyRenderer::new);
        });


    }

    private static class EmptyRenderer extends EntityRenderer<ChairEntity>
    {
        protected EmptyRenderer(EntityRendererProvider.Context ctx)
        {
            super(ctx);
        }

        @Override
        public boolean shouldRender(ChairEntity entity, Frustum camera, double camX, double camY, double camZ)
        {
            return false;
        }

        @Override
        public ResourceLocation getTextureLocation(ChairEntity entity)
        {
            return null;
        }
    }

    @SubscribeEvent
    public static void onEntityRenderersEvent(final EntityRenderersEvent.RegisterRenderers event)
    {
        if (dingusprimeKubeJSPlugin.CARTSHELF_BE != null) {
            event.registerBlockEntityRenderer(dingusprimeKubeJSPlugin.CARTSHELF_BE, new BlockEntityRendererProvider() {
                @Override
                public BlockEntityRenderer create(Context pContext) {
                    return new CartShelfBlockEntityRenderer(pContext);
                }
            });
        }
    }

    @Mod.EventBusSubscriber(modid = dingusprimeacm.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
    public class acmClientEvents_forge {

        @SubscribeEvent
        public static void onClientDisconnectionFromServer(net.minecraftforge.client.event.ClientPlayerNetworkEvent.LoggedOutEvent event) {
            try {
                GameboyScreen.lastInstance.emulator.StopEmulation();
            } catch (Exception ex) {
                //I dont care
            }
        }

    }

    private static KeyMapping[] GBBinds;
    public static GameboyController controller = new GameboyController();
    public static GameboyDisplay gameboyDisplay = new GameboyDisplay();
    public static GameboyAudio gameboyAudio = new GameboyAudio();


    public static boolean HandlePressed(int pKeyCode, int pScanCode, int pModifiers) {
        if (GBBinds != null) { //SAFETY
            for (int i = 0; i < GBBinds.length; i++) {
                KeyMapping mapping = GBBinds[i];
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
            for (int i = 0; i < GBBinds.length; i++) {
                KeyMapping mapping = GBBinds[i];
                if (mapping.isActiveAndMatches(InputConstants.getKey(pKeyCode, pScanCode))) {
                    controller.UpdateBinding(mapping, false);
                    return true;
                }
            }
        }
        return false;
    }
}


