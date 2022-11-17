package net.petercashel.dingusprimeacm.networking.packets;

import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.PacketUtils;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MerchantMenu;
import net.minecraft.world.item.trading.MerchantOffers;
import net.minecraftforge.network.NetworkEvent;
import net.petercashel.dingusprimeacm.shopkeeper.container.ShopKeeperMenu;
import net.petercashel.dingusprimeacm.shopkeeper.entity.ShopKeeper;

import java.util.function.Supplier;

public class ShopkeeperSelectTradePacket_CS {


    private final int item;

    public ShopkeeperSelectTradePacket_CS(int pItem) {
        this.item = pItem;
    }

    public ShopkeeperSelectTradePacket_CS(FriendlyByteBuf pBuffer) {
        this.item = pBuffer.readVarInt();
    }



    public static ShopkeeperSelectTradePacket_CS decoder(FriendlyByteBuf friendlyByteBuf) {
        return new ShopkeeperSelectTradePacket_CS(friendlyByteBuf);
    }


    public void encoder(FriendlyByteBuf pBuffer) {
        pBuffer.writeVarInt(this.item);
    }

    public int getItem() {
        return this.item;
    }


    public boolean messageConsumer(Supplier< NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context ctx = contextSupplier.get();
        ctx.enqueueWork(() -> {
            //Client Side
            try {
                ShopkeeperSelectTradePacket_CS pPacket = this;
                //PacketUtils.ensureRunningOnSameThread(pPacket, this, this.player.getLevel());
                int i = pPacket.getItem();
                AbstractContainerMenu abstractcontainermenu = ctx.getSender().containerMenu;
                if (abstractcontainermenu instanceof ShopKeeperMenu) {
                    ShopKeeperMenu merchantmenu = (ShopKeeperMenu)abstractcontainermenu;
                    merchantmenu.setSelectionHint(i);
                    merchantmenu.tryMoveItems(i);
                }


            } catch (Exception ex) {
                //Fail
            }

        });
        return true;
    }
}
