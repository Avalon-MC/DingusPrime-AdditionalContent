package net.petercashel.dingusprimeacm.networking.packets.shop;

import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;
import net.petercashel.dingusprimeacm.shopkeeper.container.ShopKeeperMenu;
import net.petercashel.dingusprimeacm.shopkeeper.container.ShopKeeperScreen;

import java.util.function.Supplier;

public class ShopkeeperSetResultPacket_SC {

    private final int containerId;
    private final ItemStack resultStack;

    public ShopkeeperSetResultPacket_SC(int pContainerId, ItemStack stack) {
        this.containerId = pContainerId;
        this.resultStack = stack;
    }

    public ShopkeeperSetResultPacket_SC(FriendlyByteBuf pBuffer) {
        this.containerId = pBuffer.readVarInt();
        CompoundTag tag = pBuffer.readNbt();
        resultStack = ItemStack.of(tag);
    }


//    public void handle(ClientGamePacketListener pHandler) {
//        pHandler.handleMerchantOffers(this);
//    }

    public int getContainerId() {
        return this.containerId;
    }

    public ItemStack getResultStack() {
        return this.resultStack;
    }


    public static ShopkeeperSetResultPacket_SC decoder(FriendlyByteBuf friendlyByteBuf) {
        return new ShopkeeperSetResultPacket_SC(friendlyByteBuf);
    }


    public void encoder(FriendlyByteBuf pBuffer) {
        pBuffer.writeVarInt(this.containerId);

        CompoundTag itemTag = new CompoundTag();
        resultStack.save(itemTag);
        pBuffer.writeNbt(itemTag);
    }



    public boolean messageConsumer(Supplier< NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context ctx = contextSupplier.get();
        ctx.enqueueWork(() -> {
            //Client Side
            try {
                ShopkeeperSetResultPacket_SC pPacket = this;
                //PacketUtils.ensureRunningOnSameThread(pPacket, this, Minecraft.getInstance());
                AbstractContainerMenu abstractcontainermenu = Minecraft.getInstance().player.containerMenu;
                if (pPacket.getContainerId() == abstractcontainermenu.containerId && abstractcontainermenu instanceof ShopKeeperMenu) {
                    ShopKeeperMenu merchantmenu = (ShopKeeperMenu)abstractcontainermenu;
                    merchantmenu.setResultItem(resultStack);

                    if (Minecraft.getInstance().screen instanceof ShopKeeperScreen) {
                        ShopKeeperScreen screen = (ShopKeeperScreen) Minecraft.getInstance().screen;
                        screen.updateMoney();
                    }
                }

            } catch (Exception ex) {
                //Fail
            }

        });
        return true;
    }
}
