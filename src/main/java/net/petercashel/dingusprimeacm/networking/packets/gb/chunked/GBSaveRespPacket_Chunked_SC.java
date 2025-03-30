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


public record GBSaveRespPacket_Chunked_SC (
        String CartUUID,
        int chunkID,
        byte[] saveBytes
) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<GBSaveRespPacket_Chunked_SC> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(MODID, "dingusacm_gb_save_resp_chunked_sc"));

    // Each pair of elements defines the stream codec of the element to encode/decode and the getter for the element to encode
    // The final parameter takes in the previous parameters in the order they are provided to construct the payload object
    public static final StreamCodec<ByteBuf, GBSaveRespPacket_Chunked_SC> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8,
            GBSaveRespPacket_Chunked_SC::CartUUID,
            ByteBufCodecs.INT,
            GBSaveRespPacket_Chunked_SC::chunkID,
            ByteBufCodecs.BYTE_ARRAY,
            GBSaveRespPacket_Chunked_SC::saveBytes,
            GBSaveRespPacket_Chunked_SC::new
    );


    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }



    public static Hashtable<UUID, Integer> ChunkCounts = new Hashtable<>();
    public static Hashtable<UUID, Hashtable<Integer, byte[]>> ChunkParts = new Hashtable<>();

    public static void HandlePacket(GBSaveRespPacket_Chunked_SC payload, IPayloadContext iPayloadContext) {
        int chunkID = payload.chunkID();
        String CartUUID = payload.CartUUID();
        byte[] saveBytes = payload.saveBytes();

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
                    iPayloadContext.enqueueWork(() -> {
                        //Client Side
                        AssemblePackets(payload);

                    });
                }
            }


        } else {

            UUID id = UUID.fromString(CartUUID);
            //Check if we can assemble
            if (ChunkCounts.get(id) != -1 && ChunkCounts.get(id) == ChunkParts.get(id).values().size()) {
                //WE HAVE ALL
                iPayloadContext.enqueueWork(() -> {
                    //Client Side
                    AssemblePackets(payload);

                });

            }
        }
    }

    private static void AssemblePackets(GBSaveRespPacket_Chunked_SC payload) {
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

            GameboyMenuScreen.lastInstance.emulator.StartEmulation();


        } catch (Exception ex) {
            //Fail
            System.out.println(ex);
        }
    }
}
