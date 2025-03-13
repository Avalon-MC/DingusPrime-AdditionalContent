package net.petercashel.dingusprimeacm.export.data;

import com.google.gson.annotations.Expose;
import net.minecraft.resources.ResourceLocation;
import net.petercashel.dingusprimeacm.export.data.exportEntry;

import java.util.ArrayList;

public class ItemExport extends exportEntry.exportEntryVarients {
    @Expose
    public ResourceLocation BlockKey = null;
    @Expose
    public boolean isBlockItem = false;
    @Expose
    public ArrayList BlockTags = new ArrayList<>();
    @Expose
    public int NumericID;

    public ItemExport(ResourceLocation resourceLocation) {
        super(resourceLocation);
    }
}
