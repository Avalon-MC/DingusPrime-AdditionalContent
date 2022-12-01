package net.petercashel.dingusprimeacm.shopkeeper.container;

import net.minecraft.server.level.ServerPlayer;
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
    public int containerId = -1;

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

            //Don't do client side
            if (this.PlayerReference.level.isClientSide) {
                return;
            }

            MerchantOffers merchantoffers = this.merchant_SK.getOffers();

            if (!merchantoffers.isEmpty()) {
                MerchantOffer merchantoffer = merchantoffers.getRecipeFor(ItemStack.EMPTY, ItemStack.EMPTY, this.selectionHint_sk);

                float price = merchantoffer.getPriceMultiplier();
                ItemStack resultStack = merchantoffer.assemble();

                if (!getItem(2).isEmpty() && getItem(2).equals(resultStack, false)) {
                    //same item
                    //No Charging

                } else {
                    if (ShopkeeperCurrencyHelper.canAfford(PlayerReference,price)) {
                        if (merchantoffer != null && !merchantoffer.isOutOfStock() && ShopkeeperCurrencyHelper.invoicePlayer(PlayerReference,price)) {
                            this.activeOffer = merchantoffer;
                            this.setItem(2, resultStack);
                        } else {
                            this.setItem(2, ItemStack.EMPTY);
                            this.selectionHint_sk = -1;
                        }
                    } else {
                        //On fail, do this.
                        this.setItem(2, ItemStack.EMPTY);
                        this.selectionHint_sk = -1;
                    }
                }


            } else {
                //On fail, do this.
                this.setItem(2, ItemStack.EMPTY);
                this.selectionHint_sk = -1;
            }

            this.merchant_SK.notifyTradeUpdated(this.getItem(2));

            if (containerId != -1) {
                PacketHandler.sendToPlayer(new ShopkeeperSetResultPacket_SC(this.containerId,this.getItem(2)), (ServerPlayer) this.PlayerReference);
            }


        }

    }

    @Override
    public void setSelectionHint(int pCurrentRecipeIndex) {

        if (selectionHint_sk != -1 && selectionHint_sk != pCurrentRecipeIndex) {
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

        //super.setSelectionHint(pCurrentRecipeIndex);
        updateSellItem();


    }

}
