package net.petercashel.dingusprimeacm.kubejs.types.gameboy.registry;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistryEntry;
import net.petercashel.dingusprimeacm.kubejs.RomInfoBuilder;

public class RomInfo extends ForgeRegistryEntry<RomInfo> {
    public RomInfo(RomInfoBuilder romInfoBuilder) {
        RomPath = romInfoBuilder.RomPath;
        NeedsForcedSave = romInfoBuilder.NeedsForcedSave;
        this.setRegistryName(romInfoBuilder.id);
    }

    public ResourceLocation RomPath = null;
    public boolean NeedsForcedSave = false;
}
