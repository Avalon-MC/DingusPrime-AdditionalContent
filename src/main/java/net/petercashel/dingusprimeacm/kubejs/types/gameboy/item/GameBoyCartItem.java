package net.petercashel.dingusprimeacm.kubejs.types.gameboy.item;

import dev.latvian.mods.kubejs.item.ItemBuilder;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.petercashel.dingusprimeacm.DingusPrimeAdditionalContentMod;
import net.petercashel.dingusprimeacm.kubejs.types.gameboy.datacomponent.CartItemDataComponent;

import java.util.List;
import java.util.UUID;

public class GameBoyCartItem extends Item {
    public GameBoyCartItem(Properties properties) {
        super(properties);
    }


    @Override
    public DataComponentMap components() {
        return super.components();
    }

    @Override
    public int getDefaultMaxStackSize() {
        return 1;
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {

        var stack = context.getItemInHand();
        if (stack != null && stack.isEmpty() == false && stack.getComponents().has(DingusPrimeAdditionalContentMod.CART_ITEM_DATA_COMPONENT.get()) == false) {
            CartItemDataComponent data = stack.getComponents().getOrDefault(DingusPrimeAdditionalContentMod.CART_ITEM_DATA_COMPONENT.get(), new CartItemDataComponent(gameID, UUID.randomUUID().toString()));
            stack.set(DingusPrimeAdditionalContentMod.CART_ITEM_DATA_COMPONENT.get(), data);
        }


        return InteractionResult.PASS;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {

        if (player != null) {
            var stack = player.getItemInHand(usedHand);
            if (stack != null && stack.isEmpty() == false && stack.getComponents().has(DingusPrimeAdditionalContentMod.CART_ITEM_DATA_COMPONENT.get()) == false) {
                CartItemDataComponent data = stack.getComponents().getOrDefault(DingusPrimeAdditionalContentMod.CART_ITEM_DATA_COMPONENT.get(), new CartItemDataComponent(gameID, UUID.randomUUID().toString()));
                stack.set(DingusPrimeAdditionalContentMod.CART_ITEM_DATA_COMPONENT.get(), data);
            }
        }

        return super.use(level, player, usedHand);
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
        super.inventoryTick(stack, level, entity, slotId, isSelected);
    }

    @Override
    public void onCraftedPostProcess(ItemStack stack, Level level) {
        super.onCraftedPostProcess(stack, level);
        if (stack != null && stack.isEmpty() == false && stack.getComponents().has(DingusPrimeAdditionalContentMod.CART_ITEM_DATA_COMPONENT.get()) == false) {
            CartItemDataComponent data = stack.getComponents().getOrDefault(DingusPrimeAdditionalContentMod.CART_ITEM_DATA_COMPONENT.get(), new CartItemDataComponent(gameID, UUID.randomUUID().toString()));
            stack.set(DingusPrimeAdditionalContentMod.CART_ITEM_DATA_COMPONENT.get(), data);
        }
    }

    public String gameID = "defaultrom";

    public GameBoyCartItem(GBCartridgeBuilder builder) {
        super(builder.createItemProperties()
            //.component(DingusPrimeAdditionalContentMod.CART_ITEM_DATA_COMPONENT, new CartItemDataComponent(builder.gameID, UUID.randomUUID().toString()))
        );

        gameID = builder.gameID;

        //Get component here and assign some defaults

    }


    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
        //Ensure on hover that we have the component
        if (stack.getComponents().has(DingusPrimeAdditionalContentMod.CART_ITEM_DATA_COMPONENT.get())) {
            CartItemDataComponent data = stack.getComponents().getOrDefault(DingusPrimeAdditionalContentMod.CART_ITEM_DATA_COMPONENT.get(), new CartItemDataComponent(gameID, UUID.randomUUID().toString()));
            if (!stack.getComponents().has(DingusPrimeAdditionalContentMod.CART_ITEM_DATA_COMPONENT.get())) {
                stack.set(DingusPrimeAdditionalContentMod.CART_ITEM_DATA_COMPONENT.get(), data);
            }

            if (tooltipFlag.isAdvanced()) {
                tooltipComponents.add(Component.literal("Game: " + data.GameID()));
                tooltipComponents.add(Component.literal("UUID: " + data.UniqueID()));
            }
        } else if (tooltipFlag.isCreative() == false){
            CartItemDataComponent data = new CartItemDataComponent(gameID, UUID.randomUUID().toString());
            stack.set(DingusPrimeAdditionalContentMod.CART_ITEM_DATA_COMPONENT.get(), data);
        }
    }

    @Override
    public ItemStack getDefaultInstance() {
        var stack = super.getDefaultInstance();
        return stack;
    }

    @Override
    public boolean onDroppedByPlayer(ItemStack item, Player player) {

        if (item != null && item.isEmpty() == false && item.getComponents().has(DingusPrimeAdditionalContentMod.CART_ITEM_DATA_COMPONENT.get()) == false) {
            CartItemDataComponent data = item.getComponents().getOrDefault(DingusPrimeAdditionalContentMod.CART_ITEM_DATA_COMPONENT.get(), new CartItemDataComponent(gameID, UUID.randomUUID().toString()));
            item.set(DingusPrimeAdditionalContentMod.CART_ITEM_DATA_COMPONENT.get(), data);
        }
        return super.onDroppedByPlayer(item, player);
    }

    @Override
    public InteractionResult onItemUseFirst(ItemStack stack, UseOnContext context) {

        if (stack != null && stack.isEmpty() == false && stack.getComponents().has(DingusPrimeAdditionalContentMod.CART_ITEM_DATA_COMPONENT.get()) == false) {
            CartItemDataComponent data = stack.getComponents().getOrDefault(DingusPrimeAdditionalContentMod.CART_ITEM_DATA_COMPONENT.get(), new CartItemDataComponent(gameID, UUID.randomUUID().toString()));
            stack.set(DingusPrimeAdditionalContentMod.CART_ITEM_DATA_COMPONENT.get(), data);
        }
        return super.onItemUseFirst(stack, context);
    }

    @Override
    public boolean onLeftClickEntity(ItemStack stack, Player player, Entity entity) {
        if (stack != null && stack.isEmpty() == false && stack.getComponents().has(DingusPrimeAdditionalContentMod.CART_ITEM_DATA_COMPONENT.get()) == false) {
            CartItemDataComponent data = stack.getComponents().getOrDefault(DingusPrimeAdditionalContentMod.CART_ITEM_DATA_COMPONENT.get(), new CartItemDataComponent(gameID, UUID.randomUUID().toString()));
            stack.set(DingusPrimeAdditionalContentMod.CART_ITEM_DATA_COMPONENT.get(), data);
        }
        return super.onLeftClickEntity(stack, player, entity);
    }

    @Override
    public int getMaxStackSize(ItemStack stack) {
        return 1;
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


    //Data Component Helper
    public static CartItemDataComponent GetDataComponent(ItemStack cartStack) {
        if (cartStack.getComponents().has(DingusPrimeAdditionalContentMod.CART_ITEM_DATA_COMPONENT.get())) {
            return cartStack.getComponents().get(DingusPrimeAdditionalContentMod.CART_ITEM_DATA_COMPONENT.get());
        }

        if (cartStack.getItem() instanceof GameBoyCartItem item) {
            var newComponent = new CartItemDataComponent(item.gameID, UUID.randomUUID().toString());
            cartStack.set(DingusPrimeAdditionalContentMod.CART_ITEM_DATA_COMPONENT.get(), newComponent);

            return newComponent;
        }
        return null;
    }
}
