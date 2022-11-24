package net.petercashel.dingusprimeacm.chair;

import net.minecraft.resources.ResourceLocation;
import net.petercashel.dingusprimeacm.kubejs.kubejs.CardinalBlockBuilder;

public abstract class ChairBlockBuilder extends CardinalBlockBuilder {
    public double offset;

    public ChairBlockBuilder(ResourceLocation i) {
        super(i);
    }




    public ChairBlockBuilder sitOffset(double value) {
        offset = value;

        return this;
    }
}
