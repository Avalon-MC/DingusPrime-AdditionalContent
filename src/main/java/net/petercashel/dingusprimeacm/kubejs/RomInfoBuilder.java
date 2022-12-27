package net.petercashel.dingusprimeacm.kubejs;

import dev.latvian.mods.kubejs.BuilderBase;
import dev.latvian.mods.kubejs.RegistryObjectBuilderTypes;
import net.minecraft.resources.ResourceLocation;
import net.petercashel.dingusprimeacm.kubejs.types.gameboy.registry.RomInfo;

public class RomInfoBuilder extends BuilderBase<RomInfo> {


    public ResourceLocation RomPath = null;
    public boolean NeedsForcedSave = false;

    public RomInfoBuilder(ResourceLocation i) {
        super(i);
    }

    @Override
    public RegistryObjectBuilderTypes<RomInfo> getRegistryType() {
        return dingusprimeKubeJSPlugin.ROM;
    }

    @Override
    public RomInfo createObject() {
        return new RomInfo(this);
    }

    public RomInfoBuilder romPath(String resourceLocation) {
        this.RomPath = new ResourceLocation(resourceLocation);
        return this;
    }

    public RomInfoBuilder forceSave() {
        this.NeedsForcedSave = true;
        return this;
    }

}
