package net.petercashel.dingusprimeacm.networking.packets.gb;

import io.netty.buffer.ByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.petercashel.dingusprimeacm.kubejs.types.gameboy.client.GameboyMenuScreen;
import net.petercashel.dingusprimeacm.networking.PacketHandler;
import net.petercashel.dingusprimeacm.networking.packets.gb.chunked.GBSaveRespPacket_Chunked_SC;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.function.Supplier;
import static net.petercashel.dingusprimeacm.DingusPrimeAdditionalContentMod.MODID;

public record GBSaveReqPacket_CS (String CartUUID) implements CustomPacketPayload {


    public static final CustomPacketPayload.Type<GBSaveReqPacket_CS> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(MODID, "dingusacm_gb_save_req_cs"));

    // Each pair of elements defines the stream codec of the element to encode/decode and the getter for the element to encode
    // The final parameter takes in the previous parameters in the order they are provided to construct the payload object
    public static final StreamCodec<ByteBuf, GBSaveReqPacket_CS> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8,
            GBSaveReqPacket_CS::CartUUID,
            GBSaveReqPacket_CS::new
    );


    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }



    public static void HandlePacket(GBSaveReqPacket_CS payload, IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            //Server Side
            try {


                File saveDir = new File("DingusPrime/GBSaves_Server").getAbsoluteFile();
                saveDir.mkdirs();
                File save = new File(saveDir, payload.CartUUID() + ".sav");

                if (save.exists()) {
                    if (save.length() < 16384) {
                        GBSaveRespPacket_SC response = null;
                        if (save.exists()) {
                            FileInputStream fis = new FileInputStream(save);
                            try {
                                byte[] bFile = new byte[(int) save.length()];
                                fis.read(bFile);
                                fis.close();
                                response = new GBSaveRespPacket_SC(payload.CartUUID(), bFile);
                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                            } catch (IOException e) {
                                e.printStackTrace();
                            } finally {
                                fis.close();
                            }
                        }
                        if (response != null)
                        {
                            PacketHandler.sendToPlayer(response, (ServerPlayer) ctx.player());
                        }
                    } else {

                        double partsDouble = (save.length() / 16384.0);

                        int parts = (int)partsDouble;
                        if (parts != partsDouble) {
                            parts++;
                        }

                        //ID -1 clears the array
                        PacketHandler.sendToPlayer(new GBSaveRespPacket_Chunked_SC(payload.CartUUID(), -1, new byte[] { (byte)parts }), (ServerPlayer) ctx.player());

                        FileInputStream fis = new FileInputStream(save);
                        try {
                            long remainingLength = save.length();
                            for (int i = 0; i < parts; i++) {
                                long size = 16384;
                                if (size > remainingLength) {
                                    size = remainingLength;
                                }
                                remainingLength = remainingLength - size;


                                byte[] bFile = new byte[(int) size];
                                fis.read(bFile, 0, (int) size);

                                //Send Chunks
                                PacketHandler.sendToPlayer(new GBSaveRespPacket_Chunked_SC(payload.CartUUID(), i, bFile), (ServerPlayer) ctx.player());
                            }


                            //PacketHandler.sendToPlayer(new GBSaveRespPacket_Chunked_SC(CartUUID, -2, new byte[] { (byte)parts }), ctx.getSender());

                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        } finally {
                            fis.close();
                        }

                    }
                } else {
                    //No Save

                    GBSaveRespPacket_SC response = new GBSaveRespPacket_SC(payload.CartUUID(), new byte[] {});
                    PacketHandler.sendToPlayer(response, (ServerPlayer) ctx.player());
                }

            } catch (Exception ex) {
                //Fail
            }

        });
    }
}
