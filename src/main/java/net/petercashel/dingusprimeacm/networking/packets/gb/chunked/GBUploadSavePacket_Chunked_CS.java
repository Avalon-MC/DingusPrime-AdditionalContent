package net.petercashel.dingusprimeacm.networking.packets.gb.chunked;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.petercashel.dingusprimeacm.kubejs.types.gameboy.client.GameboyMenuScreen;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.nio.ByteBuffer;
import java.util.Hashtable;
import java.util.UUID;

import static net.petercashel.dingusprimeacm.DingusPrimeAdditionalContentMod.MODID;



public record GBUploadSavePacket_Chunked_CS (
        String CartUUID,
        int chunkID,
        byte[] saveBytes
) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<GBUploadSavePacket_Chunked_CS> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(MODID, "dingusacm_gb_upload_save_chunked_cs"));

    // Each pair of elements defines the stream codec of the element to encode/decode and the getter for the element to encode
    // The final parameter takes in the previous parameters in the order they are provided to construct the payload object
    public static final StreamCodec<ByteBuf, GBUploadSavePacket_Chunked_CS> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8,
            GBUploadSavePacket_Chunked_CS::CartUUID,
            ByteBufCodecs.INT,
            GBUploadSavePacket_Chunked_CS::chunkID,
            ByteBufCodecs.BYTE_ARRAY,
            GBUploadSavePacket_Chunked_CS::saveBytes,
            GBUploadSavePacket_Chunked_CS::new
    );


    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }



    public static Hashtable<UUID, Integer> ChunkCounts = new Hashtable<>();
    public static Hashtable<UUID, Hashtable<Integer, byte[]>> ChunkParts = new Hashtable<>();

    public static void HandlePacket(GBUploadSavePacket_Chunked_CS payload, IPayloadContext iPayloadContext) {
        int chunkID = payload.chunkID();
        String CartUUID = payload.CartUUID();
        byte[] saveBytes = payload.saveBytes();

        iPayloadContext.enqueueWork(() -> {
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

                    int count = ChunkCounts.get(id);
                    int partsSize = ChunkParts.get(id).values().size();

                    //Check if we can assemble
                    if (count != -1 && count == partsSize) {
                        //WE HAVE ALL
                        AssemblePackets(payload);
                    }
                }


            } else {

                UUID id = UUID.fromString(CartUUID);
                //Check if we can assemble
                if (ChunkCounts.get(id) != -1 && ChunkCounts.get(id) == ChunkParts.get(id).values().size()) {
                    //WE HAVE ALL
                    //Client Side
                    AssemblePackets(payload);


                }
            }

        });
    }

    private static void AssemblePackets(GBUploadSavePacket_Chunked_CS payload) {
        try {
            String CartUUID = payload.CartUUID();
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


            //Todo Write Save Bytes to world emu saves folder
            if (newSaveBytes != null && newSaveBytes.capacity() != 0) {
                File saveDir = new File("DingusPrime/GBSaves_Server").getAbsoluteFile();
                File save = new File(saveDir, CartUUID + ".sav");

                byte[] output = new byte[newSaveBytes.capacity()];
                newSaveBytes.rewind();
                newSaveBytes.get(output);

                FileUtils.writeByteArrayToFile(save, output);
            }

            ChunkCounts.remove(id);
            ChunkParts.remove(id);

        } catch (Exception ex) {
            //Fail
            System.out.println(ex);
        }
    }
}

