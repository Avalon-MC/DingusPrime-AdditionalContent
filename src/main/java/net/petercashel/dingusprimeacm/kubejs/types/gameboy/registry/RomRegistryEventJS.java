package net.petercashel.dingusprimeacm.kubejs.types.gameboy.registry;

import dev.latvian.mods.kubejs.BuilderBase;
import dev.latvian.mods.kubejs.KubeJS;
import dev.latvian.mods.kubejs.RegistryObjectBuilderTypes;
import dev.latvian.mods.kubejs.event.StartupEventJS;
import dev.architectury.registry.registries.DeferredRegister;
import dev.latvian.mods.kubejs.event.StartupEventJS;
import dev.latvian.mods.kubejs.util.ConsoleJS;
import dev.latvian.mods.kubejs.util.UtilsJS;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.entity.decoration.Motive;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.npc.VillagerType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.material.Fluid;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class RomRegistryEventJS<T> extends StartupEventJS {
    private final RegistryObjectBuilderTypes<T> registry;

    public RomRegistryEventJS(RegistryObjectBuilderTypes<T> r) {
        registry = r;
    }

    public BuilderBase<? extends T> create(String id, String type) {
        var t = registry.types.get(type);

        if (t == null) {
            throw new IllegalArgumentException("Unknown type '" + type + "' for object '" + id + "'!");
        }

        var b = t.factory().createBuilder(UtilsJS.getMCID(KubeJS.appendModId(id)));

        if (b == null) {
            throw new IllegalArgumentException("Unknown type '" + type + "' for object '" + id + "'!");
        } else {
            registry.addBuilder(b);
        }

        return b;
    }

    public BuilderBase<? extends T> create(String id) {
        var t = registry.getDefaultType();

        if (t == null) {
            throw new IllegalArgumentException("Registry for type '" + registry.registryKey.location() + "' doesn't have any builders registered!");
        }

        var b = t.factory().createBuilder(UtilsJS.getMCID(KubeJS.appendModId(id)));

        if (b == null) {
            throw new IllegalArgumentException("Unknown type '" + t.type() + "' for object '" + id + "'!");
        } else {
            registry.addBuilder(b);
        }

        return b;
    }
}
