package net.petercashel.dingusprimeacm.export.data;

import com.google.gson.annotations.Expose;
import net.minecraft.resources.ResourceLocation;

public class FluidExport extends exportEntry {
    @Expose
    public ResourceLocation BucketItemKey = null;
    @Expose
    public boolean hasBucketItem = false;

    public FluidExport(ResourceLocation resourceLocation) {
        super(resourceLocation);
    }
}
