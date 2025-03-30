package net.petercashel.dingusprimeacm.networking.packets.gb;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.petercashel.dingusprimeacm.kubejs.types.gameboy.client.GameboyMenuScreen;
import org.apache.commons.io.FileUtils;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

import java.io.File;
import java.util.function.Supplier;
import static net.petercashel.dingusprimeacm.DingusPrimeAdditionalContentMod.MODID;

public record GBSaveRespPacket_SC(String CartUUID, byte[] saveBytes) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<GBSaveRespPacket_SC> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(MODID, "dingusacm_gb_save_resp_sc"));

    // Each pair of elements defines the stream codec of the element to encode/decode and the getter for the element to encode
    // The final parameter takes in the previous parameters in the order they are provided to construct the payload object
    public static final StreamCodec<ByteBuf, GBSaveRespPacket_SC> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8,
            GBSaveRespPacket_SC::CartUUID,
            ByteBufCodecs.BYTE_ARRAY,
            GBSaveRespPacket_SC::saveBytes,
            GBSaveRespPacket_SC::new
    );


    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void HandlePacket(GBSaveRespPacket_SC payload, IPayloadContext iPayloadContext) {
        iPayloadContext.enqueueWork(() -> {
            //Client Side
            try {
                if (payload.saveBytes() != null && payload.saveBytes().length != 0) {
                    File saveDir = new File("DingusPrime/GBSaves").getAbsoluteFile();
                    File save = new File(saveDir, payload.CartUUID() + ".sav");

                    FileUtils.writeByteArrayToFile(save, payload.saveBytes());
                }

                GameboyMenuScreen.lastInstance.emulator.StartEmulation();


            } catch (Exception ex) {
                //Fail
            }

        });
    }
}
