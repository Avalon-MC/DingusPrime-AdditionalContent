package net.petercashel.dingusprimeacm.networking.packets;

import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.PacketUtils;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundMerchantOffersPacket;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MerchantMenu;
import net.minecraft.world.item.trading.MerchantOffers;
import net.minecraftforge.network.NetworkEvent;
import net.petercashel.dingusprimeacm.shopkeeper.container.ShopKeeperMenu;
import org.apache.commons.io.FileUtils;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraftforge.network.NetworkEvent;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.util.function.Supplier;
import java.io.File;

public class ShopkeeperMerchantOffersPacket_SC {

    private final int containerId;
    private final MerchantOffers offers;
    private final int villagerLevel;
    private final int villagerXp;
    private final boolean showProgress;
    private final boolean canRestock;

    public ShopkeeperMerchantOffersPacket_SC(int pContainerId, MerchantOffers pOffers, int pVillagerLevel, int pVillagerXp, boolean pShowProgress, boolean pCanRestock) {
        this.containerId = pContainerId;
        this.offers = pOffers;
        this.villagerLevel = pVillagerLevel;
        this.villagerXp = pVillagerXp;
        this.showProgress = pShowProgress;
        this.canRestock = pCanRestock;
    }

    public ShopkeeperMerchantOffersPacket_SC(FriendlyByteBuf pBuffer) {
        this.containerId = pBuffer.readVarInt();
        this.offers = MerchantOffers.createFromStream(pBuffer);
        this.villagerLevel = pBuffer.readVarInt();
        this.villagerXp = pBuffer.readVarInt();
        this.showProgress = pBuffer.readBoolean();
        this.canRestock = pBuffer.readBoolean();
    }


//    public void handle(ClientGamePacketListener pHandler) {
//        pHandler.handleMerchantOffers(this);
//    }

    public int getContainerId() {
        return this.containerId;
    }

    public MerchantOffers getOffers() {
        return this.offers;
    }

    public int getVillagerLevel() {
        return this.villagerLevel;
    }

    public int getVillagerXp() {
        return this.villagerXp;
    }

    public boolean showProgress() {
        return this.showProgress;
    }

    public boolean canRestock() {
        return this.canRestock;
    }


    public static ShopkeeperMerchantOffersPacket_SC decoder(FriendlyByteBuf friendlyByteBuf) {
        return new ShopkeeperMerchantOffersPacket_SC(friendlyByteBuf);
    }


    public void encoder(FriendlyByteBuf pBuffer) {
        pBuffer.writeVarInt(this.containerId);
        this.offers.writeToStream(pBuffer);
        pBuffer.writeVarInt(this.villagerLevel);
        pBuffer.writeVarInt(this.villagerXp);
        pBuffer.writeBoolean(this.showProgress);
        pBuffer.writeBoolean(this.canRestock);
    }



    public boolean messageConsumer(Supplier< NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context ctx = contextSupplier.get();
        ctx.enqueueWork(() -> {
            //Client Side
            try {
                ShopkeeperMerchantOffersPacket_SC pPacket = this;
                //PacketUtils.ensureRunningOnSameThread(pPacket, this, Minecraft.getInstance());
                AbstractContainerMenu abstractcontainermenu = Minecraft.getInstance().player.containerMenu;
                if (pPacket.getContainerId() == abstractcontainermenu.containerId && abstractcontainermenu instanceof ShopKeeperMenu) {
                    ShopKeeperMenu merchantmenu = (ShopKeeperMenu)abstractcontainermenu;
                    merchantmenu.setOffers(new MerchantOffers(pPacket.getOffers().createTag()));
                    merchantmenu.setXp(pPacket.getVillagerXp());
                    merchantmenu.setMerchantLevel(pPacket.getVillagerLevel());
                    merchantmenu.setShowProgressBar(pPacket.showProgress());
                    merchantmenu.setCanRestock(pPacket.canRestock());


                }

            } catch (Exception ex) {
                //Fail
            }

        });
        return true;
    }
}
