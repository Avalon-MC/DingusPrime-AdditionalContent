package net.petercashel.dingusprimeacm.networking.packets.chunked;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import net.petercashel.dingusprimeacm.kubejs.types.gameboy.client.GameboyScreen;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.nio.ByteBuffer;
import java.util.Hashtable;
import java.util.UUID;
import java.util.function.Supplier;

public class GBSaveRespPacket_Chunked_SC {
    public String CartUUID;
    public int chunkID;
    public byte[] saveBytes;

    public GBSaveRespPacket_Chunked_SC() {
    }
    public GBSaveRespPacket_Chunked_SC(String cartUUID, int i, byte[] bytes) {
        CartUUID = cartUUID;
        chunkID = i;
        saveBytes = bytes;
    }

    public static GBSaveRespPacket_Chunked_SC decoder(FriendlyByteBuf friendlyByteBuf) {
        GBSaveRespPacket_Chunked_SC packet = new GBSaveRespPacket_Chunked_SC();
        int len = friendlyByteBuf.readInt();
        packet.CartUUID = friendlyByteBuf.readUtf(len);

        int lenBytes = friendlyByteBuf.readInt();
        if (lenBytes != 0) {
            packet.saveBytes = friendlyByteBuf.readByteArray(lenBytes);
        }

        packet.chunkID = friendlyByteBuf.readInt();

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
        friendlyByteBuf.writeInt(chunkID);
    }







    //Client Side Storage


    public static Hashtable<UUID, Integer> ChunkCounts = new Hashtable<>();
    public static Hashtable<UUID, Hashtable<Integer, byte[]>> ChunkParts = new Hashtable<>();


    public boolean messageConsumer(Supplier<NetworkEvent.Context> contextSupplier) {

        if (chunkID != -2) {
            //Special Handling
            UUID id = UUID.fromString(CartUUID);
            if (chunkID == -1) {
                //Clear Everything
                if (ChunkCounts.containsKey(id)) ChunkCounts.remove(id);
                ChunkCounts.put(id, (int)saveBytes[0]);

                if (ChunkParts.containsKey(id)) ChunkParts.remove(id);
                ChunkParts.put(id, new Hashtable<>());
            } else {
                ChunkParts.get(id).put(chunkID, saveBytes);

                //Check if we can assemble
                if (ChunkCounts.get(id) != -1 && ChunkCounts.get(id) == ChunkParts.get(id).values().size()) {
                    //WE HAVE ALL
                    NetworkEvent.Context ctx = contextSupplier.get();
                    ctx.enqueueWork(() -> {
                        //Client Side
                        AssemblePackets();

                    });
                }
            }


        } else {

            UUID id = UUID.fromString(CartUUID);
            //Check if we can assemble
            if (ChunkCounts.get(id) != -1 && ChunkCounts.get(id) == ChunkParts.get(id).values().size()) {
                //WE HAVE ALL
                NetworkEvent.Context ctx = contextSupplier.get();
                ctx.enqueueWork(() -> {
                    //Client Side
                    AssemblePackets();

                });

            }
        }


        return true;
    }

    private void AssemblePackets() {
        try {

            UUID id = UUID.fromString(CartUUID);
            long size = 0;

            for (byte[] a: ChunkParts.get(id).values()) {
                size += a.length;
            }

            ByteBuffer newSaveBytes = ByteBuffer.allocateDirect((int) size);

            for (int i = 0; i < ChunkCounts.get(id); i++) {
                byte[] part = ChunkParts.get(id).get(i);
                newSaveBytes.put(part, 0, part.length);
            }


            //Todo Write Save Bytes to client temp folder
            if (newSaveBytes != null && newSaveBytes.capacity() != 0) {
                File saveDir = new File("DingusPrime/GBSaves").getAbsoluteFile();
                File save = new File(saveDir, CartUUID + ".sav");

                byte[] output = new byte[newSaveBytes.capacity()];
                newSaveBytes.rewind();
                newSaveBytes.get(output);

                FileUtils.writeByteArrayToFile(save, output);
            }

            ChunkCounts.remove(id);
            ChunkParts.remove(id);

            GameboyScreen.lastInstance.emulator.StartEmulation();


        } catch (Exception ex) {
            //Fail
            System.out.println(ex);
        }
    }
}
