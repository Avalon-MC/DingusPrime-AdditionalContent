package net.petercashel.dingusprimeacm.networking.packets.gb;

import net.minecraft.network.FriendlyByteBuf;
import net.neoforged.network.NetworkEvent;
import net.petercashel.dingusprimeacm.kubejs.types.gameboy.client.GameboyScreen;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.util.function.Supplier;

public class GBSaveRespPacket_SC {
    public String CartUUID;

    public byte[] saveBytes;

    public static GBSaveRespPacket_SC decoder(FriendlyByteBuf friendlyByteBuf) {
        GBSaveRespPacket_SC packet = new GBSaveRespPacket_SC();
        int len = friendlyByteBuf.readInt();
        packet.CartUUID = friendlyByteBuf.readUtf(len);

        int lenBytes = friendlyByteBuf.readInt();
        if (lenBytes != 0) {
            packet.saveBytes = friendlyByteBuf.readByteArray(lenBytes);
        }


        return packet;
    }

    public void encoder(FriendlyByteBuf friendlyByteBuf) {
        friendlyByteBuf.writeInt(CartUUID.length());
        friendlyByteBuf.writeUtf(CartUUID);

        if (saveBytes != null) {
            friendlyByteBuf.writeInt(saveBytes.length);
            friendlyByteBuf.writeByteArray(saveBytes);

        } else {
            friendlyByteBuf.writeInt(0);
        }

    }

    public boolean messageConsumer(Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context ctx = contextSupplier.get();
        ctx.enqueueWork(() -> {
            //Client Side
            try {
                if (saveBytes != null && saveBytes.length != 0) {
                    File saveDir = new File("DingusPrime/GBSaves").getAbsoluteFile();
                    File save = new File(saveDir, CartUUID + ".sav");

                    FileUtils.writeByteArrayToFile(save, saveBytes);
                }

                GameboyScreen.lastInstance.emulator.StartEmulation();


            } catch (Exception ex) {
                //Fail
            }

        });
        return true;
    }
}
