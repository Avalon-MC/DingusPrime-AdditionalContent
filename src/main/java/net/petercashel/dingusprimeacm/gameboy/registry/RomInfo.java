package net.petercashel.dingusprimeacm.gameboy.registry;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistryEntry;
import net.minecraftforge.registries.IForgeRegistryEntry;
import net.petercashel.dingusprimeacm.kubejs.RomInfoBuilder;
import org.jetbrains.annotations.Nullable;

public class RomInfo extends ForgeRegistryEntry<RomInfo> {
    public RomInfo(RomInfoBuilder romInfoBuilder) {
        RomPath = romInfoBuilder.RomPath;
        NeedsForcedSave = romInfoBuilder.NeedsForcedSave;
        this.setRegistryName(romInfoBuilder.id);
    }

    public ResourceLocation RomPath = null;
    public boolean NeedsForcedSave = false;
}
