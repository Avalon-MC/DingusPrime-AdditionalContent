package net.petercashel.dingusprimeacm.networking.packets.zones;

import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;
import net.petercashel.dingusprimeacm.shopkeeper.container.ShopKeeperMenu;
import net.petercashel.dingusprimeacm.shopkeeper.container.ShopKeeperScreen;
import net.petercashel.dingusprimeacm.world.zones.ZoneManagerClient;
import net.petercashel.dingusprimeacm.world.zones.selection.PlayerSelectionSession;

import java.util.function.Supplier;

public class ZoneSelectionPacket_SC {

    private final PlayerSelectionSession playerSelectionSession;

    public ZoneSelectionPacket_SC(PlayerSelectionSession selectionSession) {
        this.playerSelectionSession = selectionSession;
    }

    public ZoneSelectionPacket_SC(FriendlyByteBuf pBuffer) {
        CompoundTag tag = pBuffer.readNbt();
        playerSelectionSession = new PlayerSelectionSession();
        playerSelectionSession.deserializeNBT(tag);
    }

    public static ZoneSelectionPacket_SC decoder(FriendlyByteBuf friendlyByteBuf) {
        return new ZoneSelectionPacket_SC(friendlyByteBuf);
    }


    public void encoder(FriendlyByteBuf pBuffer) {
        CompoundTag tag = playerSelectionSession.serializeNBT();
        pBuffer.writeNbt(tag);
    }



    public boolean messageConsumer(Supplier< NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context ctx = contextSupplier.get();
        ctx.enqueueWork(() -> {
            //Client Side
            try {
                ZoneManagerClient.ProcessPlayerSelection(this.playerSelectionSession);
            } catch (Exception ex) {
                //Fail
            }

        });
        return true;
    }
}
