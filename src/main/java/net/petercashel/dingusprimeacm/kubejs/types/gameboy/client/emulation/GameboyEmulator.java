package net.petercashel.dingusprimeacm.kubejs.types.gameboy.client.emulation;

import eu.rekawek.coffeegb.*;
import eu.rekawek.coffeegb.debug.Console;
import eu.rekawek.coffeegb.memory.cart.Cartridge;
import eu.rekawek.coffeegb.serial.SerialEndpoint;
import eu.rekawek.coffeegb.sound.SoundOutput;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.petercashel.dingusprimeacm.DingusPrimeAdditionalContentMod_Client;
import net.petercashel.dingusprimeacm.kubejs.types.gameboy.client.GameboyClientEvents;
import net.petercashel.dingusprimeacm.kubejs.types.gameboy.registry.RomInfo;

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

        var resman = Minecraft.getInstance().getResourceManager();
        var optionalres = resman.getResource(romFile);

        if (!optionalres.isPresent()) {
            Minecraft.getInstance().player.sendSystemMessage(Component.literal("Failed to load ROM: " + romFile.toString()));
        }

        var res = optionalres.get();
        var stream = res.open();

        byte[] byteArray = stream.readAllBytes();

        int[] romData = new int[byteArray.length];
        for (int i = 0; i < byteArray.length; i++) {
            romData[i] = byteArray[i] & 0xff;
        }

        Cartridge rom = new Cartridge(options, romData, gfb);
        this.romInstance = rom;

        SerialEndpoint serialEndpoint = SerialEndpoint.NULL_ENDPOINT;
        Optional<Console> console = Optional.empty();

        SoundOutput sound = DingusPrimeAdditionalContentMod_Client.gameboyAudio;

        DingusPrimeAdditionalContentMod_Client.controller.reset();
        DingusPrimeAdditionalContentMod_Client.gameboyDisplay.reset();

        gameboy = new Gameboy(options, rom, DingusPrimeAdditionalContentMod_Client.gameboyDisplay, DingusPrimeAdditionalContentMod_Client.controller, sound, serialEndpoint, console);

    }

    public void StartEmulation() throws IOException {
        DingusPrimeAdditionalContentMod_Client.gameboyDisplay.reset();
        new Thread(DingusPrimeAdditionalContentMod_Client.gameboyDisplay).start();
        new Thread(gameboy).start();

        gbStatus = GameboyStatus.Ready;
    }

    public void StopEmulation() {
        DingusPrimeAdditionalContentMod_Client.gameboyDisplay.stop();
        gameboy.stop();

        if (ROMInfo.NeedsForcedSave) {
            romInstance.ForcedSave(battery);
        }

        DingusPrimeAdditionalContentMod_Client.gameboyDisplay.reset();
        DingusPrimeAdditionalContentMod_Client.gameboyAudio.stop();

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
