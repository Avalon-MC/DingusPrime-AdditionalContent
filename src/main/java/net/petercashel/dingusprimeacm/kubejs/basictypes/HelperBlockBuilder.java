package net.petercashel.dingusprimeacm.kubejs.basictypes;

import dev.latvian.mods.kubejs.block.BlockBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.AABB;

public abstract class HelperBlockBuilder extends BlockBuilder {
    public HelperBlockBuilder(ResourceLocation i) {
        super(i);
    }


    public HelperBlockBuilder addBox_int(double x0, double y0, double z0, double x1, double y1, double z1, boolean flip) {
        if (flip) {
            x0 = A(x0 + x1);
            z0 = A(z0 + z1);
            this.customShape.add(new AABB(x0 / 16.0D, y0 / 16.0D, z0 / 16.0D, (x0 + x1) / 16.0D, (y0 + y1) / 16.0D, (z0 + z1) / 16.0D));
        } else {
            this.customShape.add(new AABB(x0 / 16.0D, y0 / 16.0D, z0 / 16.0D, (x0 + x1) / 16.0D, (y0 + y1) / 16.0D, (z0 + z1) / 16.0D));
        }
        return this;
    }

    private double A(double x) {
        return A(x, 0 - 32,16 + 32,16 + 32,0 - 32);
    }

    private double A(double x, double in_min, double in_max, double out_min, double out_max)
    {
        return (x - in_min) * (out_max - out_min) / (in_max - in_min) + out_min;
    }


}
