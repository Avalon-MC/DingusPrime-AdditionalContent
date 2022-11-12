package net.petercashel.dingusprimeacm.gameboy.item;

import dev.latvian.mods.kubejs.item.ItemBuilder;
import dev.latvian.mods.kubejs.item.custom.BasicItemJS;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.util.LazyOptional;
import net.petercashel.dingusprimeacm.dingusprimeacm;
import net.petercashel.dingusprimeacm.gameboy.capability.IGameBoyCartCapability;
import net.petercashel.dingusprimeacm.gameboy.registry.RomInfo;
import net.petercashel.dingusprimeacm.kubejs.dingusprimeKubeJSPlugin;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class GameBoyCartItemJS extends BasicItemJS {

    public String gameID;

    public GameBoyCartItemJS(GBCartridgeBuilder p) {
        super(p);
        gameID = p.gameID;
    }
    //defaultrom

    public static IGameBoyCartCapability GetGameboyCartCapFromStack(ItemStack stack) {
        if (stack.getItem() instanceof GameBoyCartItemJS)
        {
            LazyOptional<IGameBoyCartCapability> cap = stack.getCapability(dingusprimeacm.GAMEBOYCART_CAP_INSTANCE);
            if (cap.isPresent()) {
                IGameBoyCartCapability capability = cap.resolve().get();
                return capability;
            }
        }
        return null;
    }

    @Override
    public void appendHoverText(ItemStack stack, Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
//        LazyOptional<IGameBoyCartCapability> cap = stack.getCapability(dingusprimeacm.GAMEBOYCART_CAP_INSTANCE);
//        if (cap.isPresent()) {
//            IGameBoyCartCapability capability = cap.resolve().get();
//            if (capability.getUniqueID() == null || capability.getUniqueID().isBlank()) {
//                capability.setUniqueID(UUID.randomUUID().toString());
//            }
//            tooltip.add(new TextComponent("UUID: " + capability.getUniqueID()));
//        } else {
//            tooltip.add(new TextComponent("UUID: CAP MISSING"));
//        }

        super.appendHoverText(stack, worldIn, tooltip, flagIn);
    }



    public static class GBCartridgeBuilder extends ItemBuilder {

        String gameID = "defaultrom";

        public GBCartridgeBuilder(ResourceLocation i) {
            //super(i, 6.0F, -3.1F);
            super(i);

            this.parentModel("minecraft:item/handheld");
            this.unstackable();
            this.texture("kubejs:item/gbcart"); //default
            gameID("defaultrom");
        }

        public GameBoyCartItemJS createObject() {
            return new GameBoyCartItemJS(this);
        }

        public GBCartridgeBuilder gameID(String v) {
            this.gameID = v;
            return this;
        }
    }

}
