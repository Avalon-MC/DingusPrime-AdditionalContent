package net.petercashel.dingusprimeacm.kubejs;

import dev.architectury.registry.registries.RegistrySupplier;
import dev.latvian.mods.kubejs.BuilderBase;
import dev.latvian.mods.kubejs.CommonProperties;
import dev.latvian.mods.kubejs.RegistryObjectBuilderTypes;
import dev.latvian.mods.kubejs.item.ItemBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.petercashel.dingusprimeacm.gameboy.registry.RomInfo;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class RomInfoBuilder extends BuilderBase<RomInfo> {


    public ResourceLocation RomPath = null;

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
}
