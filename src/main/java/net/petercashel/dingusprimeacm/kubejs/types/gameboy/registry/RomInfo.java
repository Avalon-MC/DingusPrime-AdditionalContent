package net.petercashel.dingusprimeacm.kubejs.types.gameboy.registry;

import dev.latvian.mods.kubejs.core.RegistryObjectKJS;
import dev.latvian.mods.kubejs.registry.BuilderBase;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;

public class RomInfo implements RegistryObjectKJS<RomInfo> {
    public ResourceLocation RomPath = null;
    public boolean NeedsForcedSave = false;

    public RomInfo(RomInfoBuilder romInfoBuilder) {
        RomPath = romInfoBuilder.RomPath;
        NeedsForcedSave = romInfoBuilder.NeedsForcedSave;
        //this.setRegistryName(romInfoBuilder.id);

    }



    public static class RomInfoBuilder extends BuilderBase<RomInfo> {

        public ResourceLocation RomPath = null;
        public boolean NeedsForcedSave = false;

        public RomInfoBuilder(ResourceLocation id) {
            super(id);
        }

        @Override
        public RomInfo createObject() {
            return new RomInfo(this);
        }

        public RomInfoBuilder romPath(String resourceLocation) {
            this.RomPath = ResourceLocation.parse(resourceLocation);
            return this;
        }

        public RomInfoBuilder forceSave() {
            this.NeedsForcedSave = true;
            return this;
        }
    }
}
