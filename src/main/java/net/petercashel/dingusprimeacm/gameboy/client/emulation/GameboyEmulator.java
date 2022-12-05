package net.petercashel.dingusprimeacm.gameboy.client.emulation;

import eu.rekawek.coffeegb.*;
import eu.rekawek.coffeegb.debug.Console;
import eu.rekawek.coffeegb.memory.cart.Cartridge;
import eu.rekawek.coffeegb.serial.SerialEndpoint;
import eu.rekawek.coffeegb.sound.SoundOutput;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.petercashel.dingusprimeacm.dingusprimeacm_client;
import net.petercashel.dingusprimeacm.gameboy.client.GameboyClientEvents;
import net.petercashel.dingusprimeacm.gameboy.registry.RomInfo;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

public class GameboyEmulator {

    private final GameboyFileBattery battery;
    private final Cartridge romInstance;
    private Gameboy gameboy;
    public final String CartUUID;
    private final RomInfo ROMInfo;
    public GameboyStatus gbStatus = GameboyStatus.NewEmulator;


    public GameboyEmulator(RomInfo romInfo, String cartUUID) throws IOException {
        this(romInfo, cartUUID, false);

    }



    public GameboyEmulator(RomInfo romInfo, String cartUUID, boolean forceGB) throws IOException {
        this.CartUUID = cartUUID;
        this.ROMInfo = romInfo;


        ResourceLocation romFile = ROMInfo.RomPath;


        File saveDir = new File("DingusPrime/GBSaves").getAbsoluteFile();
        saveDir.mkdirs();

        GameboyFileBattery gfb = new GameboyFileBattery(saveDir, CartUUID.toString());
        this.battery = gfb;

        GameboyOptions options = new GameboyOptions(null, false, false, forceGB); //No Saves for now.
        options.DisableDebug();

        int length = Minecraft.getInstance().getResourceManager().getResource(romFile).getInputStream().readAllBytes().length;
        Cartridge rom = new Cartridge(options, Minecraft.getInstance().getResourceManager().getResource(romFile).getInputStream(), length, gfb);
        this.romInstance = rom;

        SerialEndpoint serialEndpoint = SerialEndpoint.NULL_ENDPOINT;
        Optional<Console> console = Optional.empty();

        SoundOutput sound = dingusprimeacm_client.gameboyAudio;

        dingusprimeacm_client.controller.reset();
        dingusprimeacm_client.gameboyDisplay.reset();

        gameboy = new Gameboy(options, rom, dingusprimeacm_client.gameboyDisplay, dingusprimeacm_client.controller, sound, serialEndpoint, console);

    }

    public void StartEmulation() throws IOException {
        dingusprimeacm_client.gameboyDisplay.reset();
        new Thread(dingusprimeacm_client.gameboyDisplay).start();
        new Thread(gameboy).start();

        gbStatus = GameboyStatus.Ready;
    }

    public void StopEmulation() {
        dingusprimeacm_client.gameboyDisplay.stop();
        gameboy.stop();

        if (ROMInfo.NeedsForcedSave) {
            romInstance.ForcedSave(battery);
        }

        dingusprimeacm_client.gameboyDisplay.reset();
        dingusprimeacm_client.gameboyAudio.stop();

        if (!ROMInfo.NeedsForcedSave) {
            gbStatus = GameboyStatus.UploadingSave;

            File saveDir = new File("DingusPrime/GBSaves").getAbsoluteFile();
            File save = new File(saveDir, CartUUID + ".sav");

            if (save.exists()) {
                GameboyClientEvents.UploadSave(CartUUID, save);
            }
        }

        gbStatus = GameboyStatus.Stopped;
    }
}
