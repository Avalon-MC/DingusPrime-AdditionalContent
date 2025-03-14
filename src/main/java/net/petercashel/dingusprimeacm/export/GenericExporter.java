package net.petercashel.dingusprimeacm.export;

import net.minecraft.Util;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.registries.ForgeRegistries;
import net.neoforged.registries.IForgeRegistry;
import net.neoforged.registries.IForgeRegistryEntry;
import net.neoforged.registries.tags.ITag;
import net.petercashel.dingusprimeacm.export.data.FluidExport;
import net.petercashel.dingusprimeacm.export.data.GenericExport;

import java.util.ArrayList;

public class GenericExporter<V extends IForgeRegistryEntry<V>> {

    private static void SendStatus(String s) {
        if (net.petercashel.dingusprimeacm.export.DataExporter.Player != null) {
            if (net.petercashel.dingusprimeacm.export.DataExporter.Player.isAlive()) {
                net.petercashel.dingusprimeacm.export.DataExporter.Player.sendMessage(new TextComponent(s), ChatType.GAME_INFO, Util.NIL_UUID);
            }
        }
    }

    public ArrayList<GenericExport> Export(IForgeRegistry<V> registry, ArrayList<GenericExport> genericExports) {
        genericExports = new ArrayList<>();
        int count = 1;
        SendStatus(registry.getRegistrySuperType().getSimpleName() + " " + count + " of " + registry.getKeys().size());

        for (IForgeRegistryEntry<V> entry : registry) {
            GenericExport genericExport = new GenericExport(entry.getRegistryName());
            genericExports.add(genericExport);
            count++;
        }
        return genericExports;
    }

    public ArrayList<GenericExport> ExportTags(IForgeRegistry<V> registry, ArrayList<GenericExport> genericExports) {
        genericExports = new ArrayList<>();
        int count = 1;
        SendStatus(registry.getRegistrySuperType().getSimpleName() + " Tags " + count + " of " + registry.getKeys().size());

        for (ITag<V> entry : registry.tags()) {
            GenericExport genericExport = new GenericExport(entry.getKey().location());
            genericExports.add(genericExport);
            count++;
        }

        return genericExports;
    }

    public ArrayList ExportTagsFor(IForgeRegistry<V> registry, ArrayList<GenericExport> genericExports, V value) {
        genericExports = new ArrayList<>();

        var optional = registry.tags().getReverseTag(value);

        if (optional != null && optional.isPresent() && !optional.isEmpty()) {
            var ReverseTag = optional.get();

            for (TagKey<V> entry : ReverseTag.getTagKeys().toList()) {
                GenericExport genericExport = new GenericExport(entry.location());
                genericExports.add(genericExport);
            }
        }


        return genericExports;
    }
}
