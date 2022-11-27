package net.petercashel.dingusprimeacm.mixin;


import dev.latvian.mods.kubejs.block.BlockBuilder;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.List;

@Mixin(BlockBuilder.class)
public abstract class KubeJS_BlockBuilder
{

    @Shadow
    public transient List<AABB> customShape;

    public BlockBuilder addBox(double x0, double y0, double z0, double x1, double y1, double z1, boolean flip) {
        System.out.println("BOX1 " + x0 + " " + y0 + " " + z0 + " " + x1 + " " + y1 + " " + z1 + " " + flip + " ");
        if (flip) {
            x0 = petercashel_RemapValueOverRange(x0 + x1);
            z0 = petercashel_RemapValueOverRange(z0 + z1);
            this.customShape.add(new AABB(x0 / 16.0D, y0 / 16.0D, z0 / 16.0D, (x0 + x1) / 16.0D, (y0 + y1) / 16.0D, (z0 + z1) / 16.0D));
        } else {
            this.customShape.add(new AABB(x0 / 16.0D, y0 / 16.0D, z0 / 16.0D, (x0 + x1) / 16.0D, (y0 + y1) / 16.0D, (z0 + z1) / 16.0D));
        }
        return ((BlockBuilder)(Object)this);
    }

    private double petercashel_RemapValueOverRange(double x) {
        return petercashel_RemapValueOverRange(x, 0 - 32,16 + 32,16 + 32,0 - 32);
    }

    private double petercashel_RemapValueOverRange(double x, double in_min, double in_max, double out_min, double out_max)
    {
        return (x - in_min) * (out_max - out_min) / (in_max - in_min) + out_min;
    }





}
