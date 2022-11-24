package net.petercashel.dingusprimeacm.kubejs.kubejs;

import dev.latvian.mods.kubejs.block.BlockBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.AABB;

public abstract class HelperBlockBuilder extends BlockBuilder {
    public HelperBlockBuilder(ResourceLocation i) {
        super(i);
    }


    public HelperBlockBuilder addBox(double x0, double y0, double z0, double x1, double y1, double z1, boolean flip) {
        if (flip) {
            addBox(16 - x0,16 - y0,16 - z0,16 - (x0 + x1),16 - (y0 + y1),16 - (z0 + z1), false);
        } else {
            this.customShape.add(new AABB(x0 / 16.0D, y0 / 16.0D, z0 / 16.0D, (x0 + x1) / 16.0D, (y0 + y1) / 16.0D, (z0 + z1) / 16.0D));
        }
        return this;
    }




}
