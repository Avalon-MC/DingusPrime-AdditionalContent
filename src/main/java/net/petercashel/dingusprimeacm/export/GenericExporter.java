package net.petercashel.dingusprimeacm.export;

import net.minecraft.Util;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.ChatType;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.petercashel.dingusprimeacm.export.data.GenericExport;

import java.util.ArrayList;

public class GenericExporter<V extends ResourceKey<V>> {

    private static void SendStatus(String s) {
        if (net.petercashel.dingusprimeacm.export.DataExporter.Player != null) {
            if (net.petercashel.dingusprimeacm.export.DataExporter.Player.isAlive()) {
                net.petercashel.dingusprimeacm.export.DataExporter.Player.sendSystemMessage(Component.literal(s));
            }
        }
    }

    public ArrayList<GenericExport> Export(Registry<V> registry, ArrayList<GenericExport> genericExports) {
        genericExports = new ArrayList<>();
        int count = 1;
        SendStatus(registry.key().registry().toString() + " " + count + " of " + registry.keySet().size());

        for (ResourceLocation entry : registry.keySet()) {
            GenericExport genericExport = new GenericExport(entry);
            genericExports.add(genericExport);
            count++;
        }
        return genericExports;
    }

    public ArrayList<GenericExport> ExportTags(Registry<V> registry, ArrayList<GenericExport> genericExports) {
        genericExports = new ArrayList<>();
        int count = 1;
        SendStatus(registry.key().registry().toString() + " Tags " + count + " of " + registry.keySet().size());

        for (ITag<V> entry : registry.tags()) {
            GenericExport genericExport = new GenericExport(entry.getKey().location());
            genericExports.add(genericExport);
            count++;
        }

        return genericExports;
    }

    public ArrayList ExportTagsFor(Registry<V> registry, ArrayList<GenericExport> genericExports, V value) {
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
