package net.petercashel.dingusprimeacm.networking.packets.zones;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import net.petercashel.dingusprimeacm.world.zones.ZoneManagerClient;
import net.petercashel.dingusprimeacm.world.zones.selection.PlayerSelectionSession;

import java.util.function.Supplier;

public class ZoneDataPacket_SC {

    private final CompoundTag ZoneData;

    public ZoneDataPacket_SC(CompoundTag zoneData) {
        this.ZoneData = zoneData;
    }

    public ZoneDataPacket_SC(FriendlyByteBuf pBuffer) {
        ZoneData = pBuffer.readNbt();
    }

    public static ZoneDataPacket_SC decoder(FriendlyByteBuf friendlyByteBuf) {
        return new ZoneDataPacket_SC(friendlyByteBuf);
    }


    public void encoder(FriendlyByteBuf pBuffer) {
        pBuffer.writeNbt(ZoneData);
    }



    public boolean messageConsumer(Supplier< NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context ctx = contextSupplier.get();
        ctx.enqueueWork(() -> {
            //Client Side
            try {
                ZoneManagerClient.ZoneManagerData_Client.Load(ZoneData);
                ZoneManagerClient.OnDataUpdate();
            } catch (Exception ex) {
                //Fail
            }

        });
        return true;
    }
}
