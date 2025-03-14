package net.petercashel.dingusprimeacm.networking.packets.gb;

import net.minecraft.network.FriendlyByteBuf;
import net.neoforged.network.NetworkEvent;
import net.petercashel.dingusprimeacm.networking.PacketHandler;
import net.petercashel.dingusprimeacm.networking.packets.gb.chunked.GBSaveRespPacket_Chunked_SC;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.function.Supplier;

public class GBSaveReqPacket_CS {
    public String CartUUID;

    public static GBSaveReqPacket_CS decoder(FriendlyByteBuf friendlyByteBuf) {
        GBSaveReqPacket_CS packet = new GBSaveReqPacket_CS();
        int len = friendlyByteBuf.readInt();
        packet.CartUUID = friendlyByteBuf.readUtf(len);

        return packet;
    }

    public void encoder(FriendlyByteBuf friendlyByteBuf) {
        friendlyByteBuf.writeInt(CartUUID.length());
        friendlyByteBuf.writeUtf(CartUUID);

    }

    public boolean messageConsumer(Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context ctx = contextSupplier.get();
        ctx.enqueueWork(() -> {
            //Server Side
            try {


                File saveDir = new File("DingusPrime/GBSaves_Server").getAbsoluteFile();
                saveDir.mkdirs();
                File save = new File(saveDir, CartUUID + ".sav");

                if (save.exists()) {
                    if (save.length() < 16384) {
                        GBSaveRespPacket_SC response = new GBSaveRespPacket_SC();
                        response.CartUUID = CartUUID;
                        if (save.exists()) {
                            FileInputStream fis = new FileInputStream(save);
                            try {
                                byte[] bFile = new byte[(int) save.length()];
                                fis.read(bFile);
                                fis.close();
                                response.saveBytes = bFile;
                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                            } catch (IOException e) {
                                e.printStackTrace();
                            } finally {
                                fis.close();
                            }
                        }

                        PacketHandler.sendToPlayer(response, ctx.getSender());
                    } else {

                        double partsDouble = (save.length() / 16384.0);

                        int parts = (int)partsDouble;
                        if (parts != partsDouble) {
                            parts++;
                        }

                        //ID -1 clears the array
                        PacketHandler.sendToPlayer(new GBSaveRespPacket_Chunked_SC(CartUUID, -1, new byte[] { (byte)parts }), ctx.getSender());

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
                                PacketHandler.sendToPlayer(new GBSaveRespPacket_Chunked_SC(CartUUID, i, bFile), ctx.getSender());
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

                    GBSaveRespPacket_SC response = new GBSaveRespPacket_SC();
                    response.CartUUID = CartUUID;
                    PacketHandler.sendToPlayer(response, ctx.getSender());
                }

            } catch (Exception ex) {
                //Fail
            }

        });
        return true;
    }
}
