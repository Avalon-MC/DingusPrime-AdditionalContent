package net.petercashel.dingusprimeacm.kubejs.types.cartshelf.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.petercashel.dingusprimeacm.kubejs.types.cartshelf.container.CartShelfContainer;

public class CartShelfScreen  extends AbstractContainerScreen<CartShelfContainer> {
    private final ResourceLocation GUI = new ResourceLocation("kubejs", "textures/gui/cartshelf_gui.png");

    public CartShelfScreen(CartShelfContainer container, Inventory inv, Component name) {
        super(container, inv, name);

        this.imageWidth = 180;
        this.imageHeight = 172;
    }

    @Override
    public void render(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(matrixStack);
        super.render(matrixStack, mouseX, mouseY, partialTicks);
        this.renderTooltip(matrixStack, mouseX, mouseY);
    }

    @Override
    protected void renderLabels(PoseStack matrixStack, int mouseX, int mouseY) {
        //drawString(matrixStack, Minecraft.getInstance().font, title, 10, 10, 0xffffff);
    }

    @Override
    protected void renderBg(PoseStack matrixStack, float partialTicks, int mouseX, int mouseY) {
        RenderSystem.setShaderTexture(0, GUI);
        int relX = (this.width - this.imageWidth) / 2;
        int relY = (this.height - this.imageHeight) / 2;
        this.blit(matrixStack, relX, relY, 0, 0, this.imageWidth, this.imageHeight);
    }
}
