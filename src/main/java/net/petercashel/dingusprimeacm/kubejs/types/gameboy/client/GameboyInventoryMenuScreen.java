package net.petercashel.dingusprimeacm.kubejs.types.gameboy.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.petercashel.dingusprimeacm.kubejs.types.gameboy.container.GameboyInventoryMenu;

public class GameboyInventoryMenuScreen extends AbstractContainerScreen<GameboyInventoryMenu> {

    private final ResourceLocation GUI = ResourceLocation.fromNamespaceAndPath("kubejs", "textures/gui/gameboycart_gui.png");

    public GameboyInventoryMenuScreen(GameboyInventoryMenu container, Inventory inv, Component name) {
        super(container, inv, name);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(guiGraphics, mouseX, mouseY, partialTicks);
        super.render(guiGraphics, mouseX, mouseY, partialTicks);
        this.renderTooltip(guiGraphics, mouseX, mouseY);
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        guiGraphics.drawString( Minecraft.getInstance().font, "Game Goes \\/ In That Slot", 10, 10, 0xffffff);
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTicks, int mouseX, int mouseY) {
        //RenderSystem.setShaderTexture(0, GUI);
        int relX = (this.width - this.imageWidth) / 2;
        int relY = (this.height - this.imageHeight) / 2;
        guiGraphics.blit(GUI, relX, relY, 0, 0, this.imageWidth, this.imageHeight);
    }
}
