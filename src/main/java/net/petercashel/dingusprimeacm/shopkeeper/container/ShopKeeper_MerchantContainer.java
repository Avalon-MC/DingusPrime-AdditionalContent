package net.petercashel.dingusprimeacm.shopkeeper.container;

import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MerchantContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.trading.Merchant;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.item.trading.MerchantOffers;
import net.petercashel.dingusprimeacm.networking.PacketHandler;
import net.petercashel.dingusprimeacm.networking.packets.ShopkeeperSetResultPacket_SC;

import javax.annotation.Nullable;

public class ShopKeeper_MerchantContainer extends MerchantContainer {


    private final Player PlayerReference;
    public int selectionHint_sk = -1;
    final Merchant merchant_SK;
    private static boolean doNewTradeCode = true;

    public ShopKeeper_MerchantContainer(Merchant merchant, Player player) {
        super(merchant);
        merchant_SK = merchant;
        this.PlayerReference = player;
    }


    @Override
    public boolean stillValid(Player pPlayer) {
        return true;
    }



    @Nullable
    private MerchantOffer activeOffer;

    @Override
    public void updateSellItem() {
        if (doNewTradeCode == true) {
            MerchantOffers merchantoffers = this.merchant_SK.getOffers();

            if (!merchantoffers.isEmpty()) {
                MerchantOffer merchantoffer = merchantoffers.getRecipeFor(ItemStack.EMPTY, ItemStack.EMPTY, this.selectionHint_sk);

                float price = merchantoffer.getPriceMultiplier();

                if (ShopkeeperCurrencyHelper.canAfford(PlayerReference,price)) {
                    if (merchantoffer == null || merchantoffer.isOutOfStock()) {
                        this.activeOffer = merchantoffer;
                        merchantoffer = merchantoffers.getRecipeFor(ItemStack.EMPTY, ItemStack.EMPTY, this.selectionHint_sk);
                    }

                    if (merchantoffer != null && !merchantoffer.isOutOfStock() && ShopkeeperCurrencyHelper.invoicePlayer(PlayerReference,price)) {
                        this.activeOffer = merchantoffer;
                        this.setItem(2, merchantoffer.assemble());
                    } else {
                        this.setItem(2, ItemStack.EMPTY);
                    }
                } else {
                    //On fail, do this.
                    this.setItem(2, ItemStack.EMPTY);
                }
            }

            this.merchant_SK.notifyTradeUpdated(this.getItem(2));


        } else {
            super.updateSellItem();
        }

    }

    @Override
    public void setSelectionHint(int pCurrentRecipeIndex) {

        if (selectionHint_sk != -1) {
            MerchantOffers merchantoffers = this.merchant_SK.getOffers();
            MerchantOffer merchantoffer = merchantoffers.getRecipeFor(ItemStack.EMPTY, ItemStack.EMPTY, this.selectionHint_sk);

            if (!ShopKeeperMenu.RefundNotDrop) {
                ItemStack result = merchantoffer.getResult();
                this.PlayerReference.level.addFreshEntity(new ItemEntity(this.PlayerReference.level, PlayerReference.position().x, PlayerReference.position().y, PlayerReference.position().z, result));
            } else {
                if (ShopkeeperCurrencyHelper.canRefundPlayer(PlayerReference, merchantoffer.getPriceMultiplier()))
                {
                    ShopkeeperCurrencyHelper.refundPlayer(PlayerReference, merchantoffer.getPriceMultiplier());
                }
                else
                {
                    ItemStack result = merchantoffer.getResult();
                    this.PlayerReference.level.addFreshEntity(new ItemEntity(this.PlayerReference.level, PlayerReference.position().x, PlayerReference.position().y, PlayerReference.position().z, result));
                }
            }

        }



        selectionHint_sk = pCurrentRecipeIndex;
        super.setSelectionHint(pCurrentRecipeIndex);



    }

}
