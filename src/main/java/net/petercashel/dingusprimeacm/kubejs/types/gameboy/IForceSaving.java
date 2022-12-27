package net.petercashel.dingusprimeacm.kubejs.types.gameboy;

import net.petercashel.dingusprimeacm.kubejs.types.gameboy.client.emulation.GameboyFileBattery;

public interface IForceSaving {
    void ForcedSave(GameboyFileBattery battery);
}
