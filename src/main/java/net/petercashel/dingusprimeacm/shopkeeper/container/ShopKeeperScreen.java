package net.petercashel.dingusprimeacm.shopkeeper.container;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.tm.calemieconomy.init.InitItems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.network.protocol.game.ServerboundSelectTradePacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.npc.VillagerData;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.MerchantMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.item.trading.MerchantOffers;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.ForgeRegistry;
import net.minecraftforge.registries.RegistryObject;
import net.petercashel.dingusprimeacm.networking.PacketHandler;
import net.petercashel.dingusprimeacm.networking.packets.ShopkeeperDropResultPacket_CS;
import net.petercashel.dingusprimeacm.networking.packets.ShopkeeperSelectTradePacket_CS;

public class ShopKeeperScreen extends AbstractContainerScreen<ShopKeeperMenu> {

    /** The GUI texture for the villager merchant GUI. */
    private static final ResourceLocation VILLAGER_LOCATION = new ResourceLocation("dingusprimeacm:textures/gui/shopkeeper.png");

    private static final Component TRADES_LABEL = new TranslatableComponent("merchant.dingusprimeacm.trades");
    private static final Component LEVEL_SEPARATOR = new TextComponent(" - ");
    private static final Component BALANCE_LABEL = new TextComponent("");
    private static Component BALANCE_LABEL_WORKING = new TextComponent("Balance: ");
    private static final Component DEPRECATED_TOOLTIP = new TranslatableComponent("merchant.deprecated");
    /** The integer value corresponding to the currently selected merchant recipe. */
    private int shopItem;
    private final ShopKeeperScreen.TradeOfferButton[] tradeOfferButtons = new ShopKeeperScreen.TradeOfferButton[7];
    int scrollOff;
    private boolean isDragging;
    
    public ShopKeeperScreen(ShopKeeperMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
        this.imageWidth = 276;
        this.inventoryLabelX = 107;
    }

    private void postButtonClick() {
        this.menu.setSelectionHint(this.shopItem);
        this.menu.tryMoveItems(this.shopItem);
        PacketHandler.sendToServer(new ShopkeeperSelectTradePacket_CS(this.shopItem));
        BALANCE_LABEL_WORKING = BALANCE_LABEL.copy().append(ShopkeeperCurrencyHelper.getBalanceTextComponent(this.minecraft.player));
    }

    protected void init() {
        super.init();
        int i = (this.width - this.imageWidth) / 2;
        int j = (this.height - this.imageHeight) / 2;
        int k = j + 16 + 2;

        for(int l = 0; l < 7; ++l) {
            this.tradeOfferButtons[l] = this.addRenderableWidget(new ShopKeeperScreen.TradeOfferButton(i + 5, k, l, (p_99174_) -> {
                if (p_99174_ instanceof ShopKeeperScreen.TradeOfferButton) {
                    this.shopItem = ((ShopKeeperScreen.TradeOfferButton)p_99174_).getIndex() + this.scrollOff;
                    this.postButtonClick();
                }

            }));
            k += 20;
        }

        BALANCE_LABEL_WORKING = BALANCE_LABEL.copy().append(ShopkeeperCurrencyHelper.getBalanceTextComponent(this.minecraft.player));

    }

    @Override
    public void onClose() {
        PacketHandler.sendToServer(new ShopkeeperDropResultPacket_CS());
        super.onClose();
    }

    @Override
    protected void slotClicked(Slot pSlot, int pSlotId, int pMouseButton, ClickType pType) {
        super.slotClicked(pSlot, pSlotId, pMouseButton, pType);

        if (pSlotId == 2) BALANCE_LABEL_WORKING = BALANCE_LABEL.copy().append(ShopkeeperCurrencyHelper.getBalanceTextComponent(this.minecraft.player));
    }

    protected void renderLabels(PoseStack pPoseStack, int pX, int pY) {
        int i = this.menu.getTraderLevel();
        if (i > 0 && i <= 5 && this.menu.showProgressBar()) {
            //Component component = this.title.copy().append(LEVEL_SEPARATOR).append(new TranslatableComponent("merchant.level." + i));
            int j = this.font.width(title);
            int k = 49 + this.imageWidth / 2 - j / 2;
            this.font.draw(pPoseStack, title, (float)k, 6.0F, 4210752);
        } else {
            this.font.draw(pPoseStack, this.title, (float)(49 + this.imageWidth / 2 - this.font.width(this.title) / 2), 6.0F, 4210752);
        }

        this.font.draw(pPoseStack, this.playerInventoryTitle, (float)this.inventoryLabelX, (float)this.inventoryLabelY, 4210752);
        int l = this.font.width(TRADES_LABEL);
        this.font.draw(pPoseStack, TRADES_LABEL, (float)(5 - l / 2 + 48), 6.0F, 4210752);

        int wid = this.font.width(BALANCE_LABEL_WORKING);
        this.font.draw(pPoseStack, BALANCE_LABEL_WORKING, (float)(5 - wid / 2 + 158), 42, 4210752);
    }

    protected void renderBg(PoseStack pPoseStack, float pPartialTick, int pX, int pY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, VILLAGER_LOCATION);
        int i = (this.width - this.imageWidth) / 2;
        int j = (this.height - this.imageHeight) / 2;
        blit(pPoseStack, i, j, this.getBlitOffset(), 0.0F, 0.0F, this.imageWidth, this.imageHeight, 512, 256);
        MerchantOffers merchantoffers = this.menu.getOffers();
        if (!merchantoffers.isEmpty()) {
            int k = this.shopItem;
            if (k < 0 || k >= merchantoffers.size()) {
                return;
            }

            MerchantOffer merchantoffer = merchantoffers.get(k);
            if (merchantoffer.isOutOfStock()) {
                RenderSystem.setShaderTexture(0, VILLAGER_LOCATION);
                RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
                blit(pPoseStack, this.leftPos + 83 + 99, this.topPos + 35, this.getBlitOffset(), 311.0F, 0.0F, 28, 21, 512, 256);
            }
        }
    }

    private void renderProgressBar(PoseStack pPoseStack, int pPosX, int pPosY, MerchantOffer pMerchantOffer) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, VILLAGER_LOCATION);
        int i = this.menu.getTraderLevel();
        int j = this.menu.getTraderXp();
        if (i < 5) {
            blit(pPoseStack, pPosX + 136, pPosY + 16, this.getBlitOffset(), 0.0F, 186.0F, 102, 5, 512, 256);
            int k = VillagerData.getMinXpPerLevel(i);
            if (j >= k && VillagerData.canLevelUp(i)) {
                int l = 100;
                float f = 100.0F / (float)(VillagerData.getMaxXpPerLevel(i) - k);
                int i1 = Math.min(Mth.floor(f * (float)(j - k)), 100);
                blit(pPoseStack, pPosX + 136, pPosY + 16, this.getBlitOffset(), 0.0F, 191.0F, i1 + 1, 5, 512, 256);
                int j1 = this.menu.getFutureTraderXp();
                if (j1 > 0) {
                    int k1 = Math.min(Mth.floor((float)j1 * f), 100 - i1);
                    blit(pPoseStack, pPosX + 136 + i1 + 1, pPosY + 16 + 1, this.getBlitOffset(), 2.0F, 182.0F, k1, 3, 512, 256);
                }

            }
        }
    }

    private void renderScroller(PoseStack pPoseStack, int pPosX, int pPosY, MerchantOffers pMerchantOffers) {
        int i = pMerchantOffers.size() + 1 - 7;
        if (i > 1) {
            int j = 139 - (27 + (i - 1) * 139 / i);
            int k = 1 + j / i + 139 / i;
            int l = 113;
            int i1 = Math.min(113, this.scrollOff * k);
            if (this.scrollOff == i - 1) {
                i1 = 113;
            }

            blit(pPoseStack, pPosX + 94, pPosY + 18 + i1, this.getBlitOffset(), 0.0F, 199.0F, 6, 27, 512, 256);
        } else {
            blit(pPoseStack, pPosX + 94, pPosY + 18, this.getBlitOffset(), 6.0F, 199.0F, 6, 27, 512, 256);
        }

    }

    public static final RegistryObject<Item> CopperCoin = RegistryObject.create(new ResourceLocation("calemieconomy:coin_copper"), ForgeRegistries.ITEMS);

    public void render(PoseStack pPoseStack, int pMouseX, int pMouseY, float pPartialTick) {
        this.renderBackground(pPoseStack);
        super.render(pPoseStack, pMouseX, pMouseY, pPartialTick);
        MerchantOffers merchantoffers = this.menu.getOffers();
        if (!merchantoffers.isEmpty()) {
            int i = (this.width - this.imageWidth) / 2;
            int j = (this.height - this.imageHeight) / 2;
            int k = j + 16 + 1;
            int l = i + 5 + 5;
            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            RenderSystem.setShaderTexture(0, VILLAGER_LOCATION);
            this.renderScroller(pPoseStack, i, j, merchantoffers);
            int i1 = 0;

            for(MerchantOffer merchantoffer : merchantoffers) {
                if (this.canScroll(merchantoffers.size()) && (i1 < this.scrollOff || i1 >= 7 + this.scrollOff)) {
                    ++i1;
                } else {
                    ItemStack itemstack = merchantoffer.getBaseCostA();
                    ItemStack itemstack1 = merchantoffer.getCostA();
                    ItemStack itemstack2 = merchantoffer.getCostB();
                    ItemStack itemstack3 = merchantoffer.getResult();
                    int cost = (int) merchantoffer.getPriceMultiplier();

                    if (itemstack.isEmpty() && itemstack1.isEmpty() && itemstack2.isEmpty()) {
                        itemstack = new ItemStack(CopperCoin.get(), Math.abs(cost)); //Visually show cost.
                        itemstack1 = itemstack;
                    }

                    this.itemRenderer.blitOffset = 100.0F;
                    int j1 = k + 2;
                    this.renderAndDecorateCostA(pPoseStack, itemstack1, itemstack, l, j1);
                    if (!itemstack2.isEmpty()) {
                        this.itemRenderer.renderAndDecorateFakeItem(itemstack2, i + 5 + 35, j1);
                        this.itemRenderer.renderGuiItemDecorations(this.font, itemstack2, i + 5 + 35, j1);
                    }

                    this.renderButtonArrows(pPoseStack, merchantoffer, i, j1);
                    this.itemRenderer.renderAndDecorateFakeItem(itemstack3, i + 5 + 68, j1);
                    this.itemRenderer.renderGuiItemDecorations(this.font, itemstack3, i + 5 + 68, j1);
                    this.itemRenderer.blitOffset = 0.0F;
                    k += 20;
                    ++i1;
                }
            }

            int k1 = this.shopItem;
            MerchantOffer merchantoffer1 = merchantoffers.get(k1);
            if (this.menu.showProgressBar()) {
                //this.renderProgressBar(pPoseStack, i, j, merchantoffer1);
            }

            if (merchantoffer1.isOutOfStock() && this.isHovering(186, 35, 22, 21, (double)pMouseX, (double)pMouseY) && this.menu.canRestock()) {
                this.renderTooltip(pPoseStack, DEPRECATED_TOOLTIP, pMouseX, pMouseY);
            }

            for(ShopKeeperScreen.TradeOfferButton ShopKeeperScreen$tradeofferbutton : this.tradeOfferButtons) {
                if (ShopKeeperScreen$tradeofferbutton.isHoveredOrFocused()) {
                    ShopKeeperScreen$tradeofferbutton.renderToolTip(pPoseStack, pMouseX, pMouseY);
                }

                ShopKeeperScreen$tradeofferbutton.visible = ShopKeeperScreen$tradeofferbutton.index < this.menu.getOffers().size();
            }

            RenderSystem.enableDepthTest();
        }

        this.renderTooltip(pPoseStack, pMouseX, pMouseY);
    }

    private void renderButtonArrows(PoseStack pPoseStack, MerchantOffer pMerchantOffer, int pPosX, int pPosY) {
        RenderSystem.enableBlend();
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, VILLAGER_LOCATION);
        if (pMerchantOffer.isOutOfStock()) {
            blit(pPoseStack, pPosX + 5 + 35 + 20, pPosY + 3, this.getBlitOffset(), 25.0F, 171.0F, 10, 9, 512, 256);
        } else {
            blit(pPoseStack, pPosX + 5 + 35 + 20, pPosY + 3, this.getBlitOffset(), 15.0F, 171.0F, 10, 9, 512, 256);
        }

    }

    private void renderAndDecorateCostA(PoseStack pPoseStack, ItemStack pRealCost, ItemStack pBaseCost, int pX, int pY) {
        this.itemRenderer.renderAndDecorateFakeItem(pRealCost, pX, pY);
        if (pBaseCost.getCount() == pRealCost.getCount()) {
            this.itemRenderer.renderGuiItemDecorations(this.font, pRealCost, pX, pY);
        } else {
            this.itemRenderer.renderGuiItemDecorations(this.font, pBaseCost, pX, pY, pBaseCost.getCount() == 1 ? "1" : null);
            this.itemRenderer.renderGuiItemDecorations(this.font, pRealCost, pX + 14, pY, pRealCost.getCount() == 1 ? "1" : null);
            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            RenderSystem.setShaderTexture(0, VILLAGER_LOCATION);
            this.setBlitOffset(this.getBlitOffset() + 300);
            blit(pPoseStack, pX + 7, pY + 12, this.getBlitOffset(), 0.0F, 176.0F, 9, 2, 512, 256);
            this.setBlitOffset(this.getBlitOffset() - 300);
        }

    }

    private boolean canScroll(int pNumOffers) {
        return pNumOffers > 7;
    }

    public boolean mouseScrolled(double pMouseX, double pMouseY, double pDelta) {
        int i = this.menu.getOffers().size();
        if (this.canScroll(i)) {
            int j = i - 7;
            this.scrollOff = Mth.clamp((int)((double)this.scrollOff - pDelta), 0, j);
        }

        return true;
    }

    public boolean mouseDragged(double pMouseX, double pMouseY, int pButton, double pDragX, double pDragY) {
        int i = this.menu.getOffers().size();
        if (this.isDragging) {
            int j = this.topPos + 18;
            int k = j + 139;
            int l = i - 7;
            float f = ((float)pMouseY - (float)j - 13.5F) / ((float)(k - j) - 27.0F);
            f = f * (float)l + 0.5F;
            this.scrollOff = Mth.clamp((int)f, 0, l);
            return true;
        } else {
            return super.mouseDragged(pMouseX, pMouseY, pButton, pDragX, pDragY);
        }
    }

    public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {
        this.isDragging = false;
        int i = (this.width - this.imageWidth) / 2;
        int j = (this.height - this.imageHeight) / 2;
        if (this.canScroll(this.menu.getOffers().size()) && pMouseX > (double)(i + 94) && pMouseX < (double)(i + 94 + 6) && pMouseY > (double)(j + 18) && pMouseY <= (double)(j + 18 + 139 + 1)) {
            this.isDragging = true;
        }

        return super.mouseClicked(pMouseX, pMouseY, pButton);
    }

    @OnlyIn(Dist.CLIENT)
    class TradeOfferButton extends Button {
        final int index;

        public TradeOfferButton(int pX, int pY, int pIndex, Button.OnPress pOnPress) {
            super(pX, pY, 89, 20, TextComponent.EMPTY, pOnPress);
            this.index = pIndex;
            this.visible = false;
        }

        public int getIndex() {
            return this.index;
        }

        public void renderToolTip(PoseStack pPoseStack, int pMouseX, int pMouseY) {
            if (this.isHovered && ShopKeeperScreen.this.menu.getOffers().size() > this.index + ShopKeeperScreen.this.scrollOff) {
                if (pMouseX < this.x + 20) {
                    ItemStack itemstack = ShopKeeperScreen.this.menu.getOffers().get(this.index + ShopKeeperScreen.this.scrollOff).getCostA();

                    //Get correct tooltip item
                    int cost = (int) ShopKeeperScreen.this.menu.getOffers().get(this.index + ShopKeeperScreen.this.scrollOff).getPriceMultiplier();
                    if (itemstack.isEmpty()) {
                        itemstack = new ItemStack(CopperCoin.get(), Math.abs(cost)); //Visually show cost.
                    }

                    ShopKeeperScreen.this.renderTooltip(pPoseStack, itemstack, pMouseX, pMouseY);
                } else if (pMouseX < this.x + 50 && pMouseX > this.x + 30) {
                    ItemStack itemstack2 = ShopKeeperScreen.this.menu.getOffers().get(this.index + ShopKeeperScreen.this.scrollOff).getCostB();
                    if (!itemstack2.isEmpty()) {
                        ShopKeeperScreen.this.renderTooltip(pPoseStack, itemstack2, pMouseX, pMouseY);
                    }
                } else if (pMouseX > this.x + 65) {
                    ItemStack itemstack1 = ShopKeeperScreen.this.menu.getOffers().get(this.index + ShopKeeperScreen.this.scrollOff).getResult();
                    ShopKeeperScreen.this.renderTooltip(pPoseStack, itemstack1, pMouseX, pMouseY);
                }
            }

        }
    }
}
