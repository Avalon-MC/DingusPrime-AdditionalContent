package net.petercashel.dingusprimeacm.gameboy.client;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.petercashel.dingusprimeacm.dingusprimeacm;
import net.petercashel.dingusprimeacm.gameboy.client.emulation.GameboyStatus;
import net.petercashel.dingusprimeacm.networking.PacketHandler;
import net.petercashel.dingusprimeacm.networking.packets.GBUploadSavePacket_CS;
import net.petercashel.dingusprimeacm.networking.packets.chunked.GBUploadSavePacket_Chunked_CS;

import java.io.*;
import java.nio.file.Files;

public class GameboyClientEvents {


    public static void UploadSave(String uuid, File saveFile) {
        GameboyScreen.lastInstance.emulator.gbStatus = GameboyStatus.UploadingSave;
        //TODO
        System.out.println("Save was called " + saveFile.getAbsolutePath());

        if (saveFile.length() < 16384) {
            GBUploadSavePacket_CS savePacket = new GBUploadSavePacket_CS();
            savePacket.CartUUID = uuid;
            try {
                FileInputStream fis = new FileInputStream(saveFile);
                byte[] bFile = new byte[(int) saveFile.length()];
                fis.read(bFile);
                fis.close();
                savePacket.saveBytes = bFile;
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            PacketHandler.sendToServer(savePacket);
            GameboyClientEvents.SetGBStatus(GameboyStatus.Ready);
        } else {
            //Chunked Upload.

            File save = saveFile;
            String CartUUID = uuid;

            double partsDouble = (save.length() / 16384.0);

            int parts = (int)partsDouble;
            if (parts != partsDouble) {
                parts++;
            }

            //ID -1 clears the array
            PacketHandler.sendToServer(new GBUploadSavePacket_Chunked_CS(CartUUID, -1, new byte[] { (byte)parts }));

            FileInputStream fis = null;
            try {
                fis = new FileInputStream(save);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
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
                    PacketHandler.sendToServer(new GBUploadSavePacket_Chunked_CS(CartUUID, i, bFile));
                }


                //PacketHandler.sendToServer(new GBUploadSavePacket_Chunked_CS(CartUUID, -2, new byte[] { (byte)parts }));

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }


            GameboyClientEvents.SetGBStatus(GameboyStatus.Ready);
        }
    }

    static void SetGBStatus(GameboyStatus ready) {

        if (GameboyScreen.lastInstance != null && GameboyScreen.lastInstance.emulator != null) {
            GameboyScreen.lastInstance.emulator.gbStatus = GameboyStatus.Ready;
        }

    }
}
