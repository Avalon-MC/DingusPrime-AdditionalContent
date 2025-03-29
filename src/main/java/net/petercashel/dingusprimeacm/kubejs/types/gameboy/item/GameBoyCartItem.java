package net.petercashel.dingusprimeacm.kubejs.types.gameboy.item;

import dev.latvian.mods.kubejs.item.ItemBuilder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.petercashel.dingusprimeacm.DingusPrimeAdditionalContentMod;
import net.petercashel.dingusprimeacm.DingusRegistries;
import net.petercashel.dingusprimeacm.kubejs.types.gameboy.datacomponent.CartItemDataComponent;

import java.util.List;
import java.util.UUID;

public class GameBoyCartItem extends Item {
    public GameBoyCartItem(Properties properties) {
        super(properties);
    }

    public String gameID = "defaultrom";

    public GameBoyCartItem(GBCartridgeBuilder builder) {
        super(builder.createItemProperties()
            .component(DingusPrimeAdditionalContentMod.CART_ITEM_DATA_COMPONENT, new CartItemDataComponent(builder.gameID, UUID.randomUUID().toString()))
        );

        gameID = builder.gameID;

        //Get component here and assign some defaults

    }


    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
        //Add tooltip here

    }


    public static class GBCartridgeBuilder extends ItemBuilder
    {
        String gameID = "defaultrom";
        public GBCartridgeBuilder(ResourceLocation id) {
            super(id);

            this.parentModel(ResourceLocation.parse("minecraft:item/handheld"));
            this.unstackable();
            this.texture("kubejs:item/gbcart"); //default
            gameID("defaultrom");


        }

        public GameBoyCartItem createObject() {
            return new GameBoyCartItem(this);
        }

        public GBCartridgeBuilder gameID(String v) {
            this.gameID = v;
            return this;
        }
    }
}
