package net.petercashel.dingusprimeacm.networking.packets.shop;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraftforge.network.NetworkEvent;
import net.petercashel.dingusprimeacm.shopkeeper.container.ShopKeeperMenu;

import java.util.function.Supplier;

public class ShopkeeperDropResultPacket_CS {



    public ShopkeeperDropResultPacket_CS() {

    }

    public ShopkeeperDropResultPacket_CS(FriendlyByteBuf pBuffer) {

    }



    public static ShopkeeperDropResultPacket_CS decoder(FriendlyByteBuf friendlyByteBuf) {
        return new ShopkeeperDropResultPacket_CS(friendlyByteBuf);
    }


    public void encoder(FriendlyByteBuf pBuffer) {

    }



    public boolean messageConsumer(Supplier< NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context ctx = contextSupplier.get();
        ctx.enqueueWork(() -> {
            //Client Side
            try {
                ShopkeeperDropResultPacket_CS pPacket = this;
                //PacketUtils.ensureRunningOnSameThread(pPacket, this, this.player.getLevel());
                AbstractContainerMenu abstractcontainermenu = ctx.getSender().containerMenu;
                if (abstractcontainermenu instanceof ShopKeeperMenu) {
                    ShopKeeperMenu merchantmenu = (ShopKeeperMenu)abstractcontainermenu;
                    merchantmenu.dropResultItem();
                }


            } catch (Exception ex) {
                //Fail
            }

        });
        return true;
    }
}
