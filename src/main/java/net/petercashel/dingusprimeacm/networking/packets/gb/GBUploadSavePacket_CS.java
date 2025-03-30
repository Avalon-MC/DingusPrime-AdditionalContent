package net.petercashel.dingusprimeacm.networking.packets.gb;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.petercashel.dingusprimeacm.kubejs.types.gameboy.client.GameboyMenuScreen;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.util.function.Supplier;
import static net.petercashel.dingusprimeacm.DingusPrimeAdditionalContentMod.MODID;


public record GBUploadSavePacket_CS(String CartUUID, byte[] saveBytes) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<GBUploadSavePacket_CS> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(MODID, "dingusacm_gb_upload_save_cs"));

    // Each pair of elements defines the stream codec of the element to encode/decode and the getter for the element to encode
    // The final parameter takes in the previous parameters in the order they are provided to construct the payload object
    public static final StreamCodec<ByteBuf, GBUploadSavePacket_CS> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8,
            GBUploadSavePacket_CS::CartUUID,
            ByteBufCodecs.BYTE_ARRAY,
            GBUploadSavePacket_CS::saveBytes,
            GBUploadSavePacket_CS::new
    );


    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void HandlePacket(GBUploadSavePacket_CS payload, IPayloadContext iPayloadContext) {
        iPayloadContext.enqueueWork(() -> {
            //Server Side
            try {
                //Todo Write Save Bytes to world emu saves folder

                if (payload.saveBytes() != null && payload.saveBytes().length != 0) {
                    File saveDir = new File("DingusPrime/GBSaves_Server").getAbsoluteFile();
                    saveDir.mkdirs();
                    File save = new File(saveDir, payload.CartUUID() + ".sav");

                    FileUtils.writeByteArrayToFile(save, payload.saveBytes());
                }


            } catch (Exception ex) {
                //Fail
            }

        });
    }
}
