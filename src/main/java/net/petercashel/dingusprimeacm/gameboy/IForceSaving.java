package net.petercashel.dingusprimeacm.gameboy;

import net.petercashel.dingusprimeacm.gameboy.client.emulation.GameboyFileBattery;

public interface IForceSaving {
    void ForcedSave(GameboyFileBattery battery);
}
