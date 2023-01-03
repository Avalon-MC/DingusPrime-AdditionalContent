package net.petercashel.dingusprimeacm.creative;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

import static net.petercashel.dingusprimeacm.dingusprimeacm.ZONETOOLITEM;

public class DPACM_MainTab extends CreativeModeTab {
    public DPACM_MainTab(int index, String label) {
        super(index, label);
    }

    @Override
    public ItemStack makeIcon() {
        return new ItemStack(ZONETOOLITEM.get());
    }
}
