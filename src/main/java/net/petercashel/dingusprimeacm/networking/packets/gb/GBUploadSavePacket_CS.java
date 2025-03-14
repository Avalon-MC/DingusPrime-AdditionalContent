package net.petercashel.dingusprimeacm.networking.packets.gb;

import net.minecraft.network.FriendlyByteBuf;
import net.neoforged.network.NetworkEvent;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.util.function.Supplier;

public class GBUploadSavePacket_CS {

    public String CartUUID;

    public byte[] saveBytes;

    public static GBUploadSavePacket_CS decoder(FriendlyByteBuf friendlyByteBuf) {
        GBUploadSavePacket_CS packet = new GBUploadSavePacket_CS();
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
            //Server Side
            try {
                //Todo Write Save Bytes to world emu saves folder

                if (saveBytes != null && saveBytes.length != 0) {
                    File saveDir = new File("DingusPrime/GBSaves_Server").getAbsoluteFile();
                    saveDir.mkdirs();
                    File save = new File(saveDir, CartUUID + ".sav");

                    FileUtils.writeByteArrayToFile(save, saveBytes);
                }


            } catch (Exception ex) {
                //Fail
            }

        });
        return true;
    }
}
