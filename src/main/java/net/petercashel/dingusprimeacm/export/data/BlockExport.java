package net.petercashel.dingusprimeacm.export.data;

import com.google.gson.annotations.Expose;
import net.minecraft.resources.ResourceLocation;

public class BlockExport extends exportEntry.exportEntryVarients {
    @Expose
    public ResourceLocation BlockItemKey = null;
    @Expose
    public boolean hasBlockItem = false;

    public BlockExport(ResourceLocation resourceLocation) {
        super(resourceLocation);
    }
}
