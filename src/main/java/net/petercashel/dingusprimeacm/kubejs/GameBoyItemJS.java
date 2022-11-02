package net.petercashel.dingusprimeacm.kubejs;

import dev.latvian.mods.kubejs.item.ItemBuilder;
import dev.latvian.mods.kubejs.item.custom.BasicItemJS;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.util.LazyOptional;
import net.petercashel.dingusprimeacm.dingusprimeacm;
import net.petercashel.dingusprimeacm.gameboy.capability.IGameBoyCapability;
import net.petercashel.dingusprimeacm.gameboy.registry.RomInfo;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class GameBoyItemJS extends BasicItemJS {

    private String gameID;

    public GameBoyItemJS(GameBoyBuilder p) {
        super(p);
        gameID = p.gameID;
    }


//    @Nullable
//    @Override
//    public CompoundTag getShareTag(ItemStack stack) {
//        CompoundTag tag = super.getShareTag(stack);
//
//        LazyOptional<IGameBoyCapability> cap = stack.getCapability(dingusprimeacm.GAMEBOY_CAP_INSTANCE);
//        if (cap.isPresent()) {
//            IGameBoyCapability capability = cap.resolve().get();
//            if (capability.getUniqueID() == null || capability.getUniqueID().isBlank()) {
//                capability.setUniqueID(UUID.randomUUID().toString());
//            }
//            tag.put("gameboycap", capability.serializeNBT());
//        }
//
//        return tag;
//    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {
        return super.use(world, player, hand);
    }

    @Override
    public void appendHoverText(ItemStack stack, Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
        tooltip.add(new TextComponent("Game: " + gameID));

        if (dingusprimeKubeJSPlugin.ROM_REGISTRY != null) {

            Optional<RomInfo> romInfoOptional = dingusprimeKubeJSPlugin.ROM_REGISTRY.get().getValues().stream().filter(x -> x.getRegistryName().toString().contains(gameID)).findFirst();
            if (romInfoOptional.isPresent() && !romInfoOptional.isEmpty()) {
                tooltip.add(new TextComponent("ROM: " + romInfoOptional.get().RomPath.toString()));
            } else {
                tooltip.add(new TextComponent("ROM: Registry Missing Entry"));
            }
        } else {
            tooltip.add(new TextComponent("ROM: Registry Error"));
        }

        LazyOptional<IGameBoyCapability> cap = stack.getCapability(dingusprimeacm.GAMEBOY_CAP_INSTANCE);
        if (cap.isPresent()) {
            IGameBoyCapability capability = cap.resolve().get();
            if (capability.getUniqueID() == null || capability.getUniqueID().isBlank()) {
                capability.setUniqueID(UUID.randomUUID().toString());
            }
            tooltip.add(new TextComponent("UUID: " + capability.getUniqueID()));
        } else {
            tooltip.add(new TextComponent("UUID: CAP MISSING"));
        }



        super.appendHoverText(stack, worldIn, tooltip, flagIn);
    }



    public static class GameBoyBuilder extends ItemBuilder {

        String gameID = "";

        public GameBoyBuilder(ResourceLocation i) {
            //super(i, 6.0F, -3.1F);
            super(i);

            this.parentModel("minecraft:item/handheld");
            this.unstackable();
        }

        public GameBoyItemJS createObject() {
            return new GameBoyItemJS(this);
        }

        public GameBoyBuilder gameID(String v) {
            this.gameID = v;
            return this;
        }
    }

}
