package net.petercashel.dingusprimeacm.kubejs.types.gameboy.client;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.petercashel.dingusprimeacm.dingusprimeacm_client;
import net.petercashel.dingusprimeacm.kubejs.types.gameboy.client.emulation.GameboyEmulator;
import net.petercashel.dingusprimeacm.kubejs.types.gameboy.client.emulation.GameboyStatus;
import net.petercashel.dingusprimeacm.kubejs.types.gameboy.container.GameboyContainer;
import net.petercashel.dingusprimeacm.kubejs.types.gameboy.registry.RomInfo;
import net.petercashel.dingusprimeacm.networking.PacketHandler;
import net.petercashel.dingusprimeacm.networking.packets.gb.GBSaveReqPacket_CS;

import java.awt.image.BufferedImage;
import java.io.IOException;

public class GameboyScreen extends AbstractContainerScreen<GameboyContainer> {

    private final ResourceLocation GUI = new ResourceLocation("dingusprimeacm", "textures/gui/gameboy_gui_default.png");
    private ResourceLocation UIBack;

    public static final BufferedImage GBImageBuffer = new BufferedImage(160, 144, BufferedImage.TYPE_INT_RGB);
    public static final DynamicTexture imageBuffer = new DynamicTexture(160, 144, false);
    public static ResourceLocation dynamicResource;

    public boolean ShowControls = true;
    public int[] videoPos = new int[] { 46,23 };
    public int[] videoSize = new int[] { 89,80 };
    public int[] statusPos = new int[] { 23,38 };
    public int[] statusSize = new int[] { 22,16 };

    public static GameboyScreen lastInstance;
    public GameboyEmulator emulator;
    GameboyStatus gbStatusCurrent = GameboyStatus.NewEmulator;
    int statusTicks = -1;
    public String GameID;

    public GameboyScreen(GameboyContainer container, Inventory inv, Component name) {
        super(container, inv, name);
        lastInstance = this;

        if (dynamicResource == null) {
            int color = NativeImage.combine(255, 1, 1, 128);
            for (int x = 0; x < 160; x++) {
                for (int y = 0; y < 144; y++)
                {
                    GBImageBuffer.setRGB(x,y,color);
                    imageBuffer.getPixels().setPixelRGBA(x,y,color);
                }
            }

            imageBuffer.upload();
            dynamicResource = Minecraft.getInstance().textureManager.register("gameboytex", imageBuffer);
        }

        UIBack = container.GetUIBack(GUI);
    }

    public void SetupEmulator(String GameID, String CartUUID)
    {
        this.GameID = GameID;
        System.out.println("GameID: " + GameID);
        System.out.println("CartUUID: " + CartUUID);
        RomInfo rom = menu.GetRomInfo(GameID);

        if (rom != null) {
            try {
                emulator = new GameboyEmulator(rom, CartUUID, menu.ForceGB);

                GBSaveReqPacket_CS saveRequest = new GBSaveReqPacket_CS();
                saveRequest.CartUUID = CartUUID;

                GameboyClientEvents.SetGBStatus(GameboyStatus.DownloadingSave);
                PacketHandler.sendToServer(saveRequest);

                //emulator.StartEmulation(); Elsewhere now
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void render(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(matrixStack);
        super.render(matrixStack, mouseX, mouseY, partialTicks);
        this.renderTooltip(matrixStack, mouseX, mouseY);


    }

    @Override
    protected void renderLabels(PoseStack matrixStack, int mouseX, int mouseY) {

        if (ShowControls && false) {
            drawString(matrixStack, Minecraft.getInstance().font, "SOME TEXT", 41, 147, 0xffffff);

        }

    }

    private GameboyStatus getStatus() {
        if (emulator == null) return GameboyStatus.NewEmulator;
        return emulator.gbStatus;
    }

    public int GetStatusColor() {
        if (emulator == null) {
            return 0xffffffff;
        }


        switch (emulator.gbStatus) {

            case NewEmulator -> {
                return 0xff000000;
            }
            case DownloadingSave -> {
                return 0xff00aaaa;
            }
            case Ready -> {
                return 0xffff0000;
            }
            case UploadingSave -> {
                return 0xff00aaaa;
            }
            case Stopped -> {
                return 0xff0000aa;
            }
            case Error -> {
                return 0xffffffff;
            }
        }


        return 0xffffffff;
    }

    @Override
    protected void renderBg(PoseStack matrixStack, float partialTicks, int mouseX, int mouseY) {
        int relX = (this.width - this.imageWidth) / 2;
        int relY = (this.height - this.imageHeight) / 2;

        //Status Tick
        statusTicks++;
        if (statusTicks > 10) {
            statusTicks = 0;
            gbStatusCurrent = getStatus();
        }

        //Status
        AbstractContainerScreen.fill(matrixStack,relX + statusPos[0], relY+ statusPos[1], relX+ statusPos[0] + statusSize[0], relY+ statusPos[1]+ statusSize[1], GetStatusColor());




        RenderSystem.setShaderTexture(0, UIBack);
        this.blit(matrixStack, relX, relY, 0, 0, this.imageWidth, 256);

        RenderSystem.setShaderTexture(0, dynamicResource);
        blit(matrixStack, relX + videoPos[0], relY+ videoPos[1], 0, 0, videoSize[0], videoSize[1], videoSize[0], videoSize[1]);
    }


    @Override
    public boolean keyPressed(int pKeyCode, int pScanCode, int pModifiers)
    {
        //https://www.glfw.org/docs/3.3/group__keys.html

        //GLFW_KEY_ESCAPE
        //GLFW_KEY_Z
        //GLFW_KEY_X
        //GLFW_KEY_UP
        //GLFW_KEY_DOWN
        //GLFW_KEY_LEFT
        //GLFW_KEY_RIGHT

        if (dingusprimeacm_client.HandlePressed(pKeyCode, pScanCode, pModifiers)) {
            return true;
        }

        return super.keyPressed(pKeyCode, pScanCode, pModifiers);
    }

    @Override
    public boolean keyReleased(int pKeyCode, int pScanCode, int pModifiers) {
        if (dingusprimeacm_client.HandleReleased(pKeyCode, pScanCode, pModifiers)) {
            return true;
        }
        return super.keyReleased(pKeyCode, pScanCode, pModifiers);
    }

    @Override
    protected void containerTick() {
        super.containerTick();
    }

    @Override
    public GuiEventListener getFocused() {
        return super.getFocused();
    }

    @Override
    public void onClose() {
        super.onClose();
        if (emulator != null) {
            emulator.StopEmulation();
        }
    }
}
