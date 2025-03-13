package net.petercashel.dingusprimeacm.export.data;

import com.google.gson.annotations.Expose;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;

public abstract class exportEntry {
    @Expose
    public ResourceLocation resourceLocation;

    public exportEntry(ResourceLocation resourceLocation) {
        this.resourceLocation = resourceLocation;
    }

    public static abstract class exportEntryVarients extends exportEntry {

        @Expose
        public ArrayList<exportVarient> varients = new ArrayList<>();
        @Expose
        public ArrayList Tags = new ArrayList<>();

        public exportEntryVarients(ResourceLocation resourceLocation) {
            super(resourceLocation);
        }
    }
}


