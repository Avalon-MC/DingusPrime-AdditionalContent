package net.petercashel.dingusprimeacm.flatpack;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import dev.latvian.mods.kubejs.block.custom.BasicBlockJS;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.petercashel.dingusprimeacm.cartshelf.block.CartShelfBlockEntity;
import net.petercashel.dingusprimeacm.cartshelf.container.CartShelfContainer;
import net.petercashel.dingusprimeacm.kubejs.ShopTradeInfoBuilder;
import net.petercashel.dingusprimeacm.kubejs.kubejs.HelperBlockBuilder;

import java.util.ArrayList;

public class FlatpackBlockJS extends BasicBlockJS {

    public static class FlatPackData {
        public final String ResourceName;
        public final int Amount;

        public FlatPackData(String resourceName, int amount) {
            ResourceName = resourceName;
            Amount = amount;
        }

    }

    public final ArrayList<FlatPackData> ItemsToCreate;

    public FlatpackBlockJS(FlatpackBuilder p) {
        super(p);
        ItemsToCreate = p.ItemsToCreate;
    }

    @Override
    public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
        if (!pLevel.isClientSide && pHand == InteractionHand.MAIN_HAND)
        {
            for (var ItemData: ItemsToCreate) {
                ResourceLocation location = GetResourceLocation(ItemData.ResourceName);
                ItemStack stack = GetItemStack(location, ItemData.Amount);

                if (stack != null && stack.isEmpty() == false) {
                    pLevel.addFreshEntity(new ItemEntity(pPlayer.level, pPlayer.position().x, pPlayer.position().y, pPlayer.position().z, stack));
                }
            }

            pLevel.destroyBlock(pPos, false);

            return InteractionResult.SUCCESS;
        }


        return super.use(pState, pLevel, pPos, pPlayer, pHand, pHit);
    }

    private ItemStack GetItemStack(ResourceLocation resourceLocation, int count) {

        ResourceLocation location = new ResourceLocation("kubejs", resourceLocation.getPath());
        try {
            RegistryObject<Item> item = RegistryObject.create(resourceLocation, ForgeRegistries.ITEMS);
            if (item.isPresent()) {
                return new ItemStack(item.get().asItem(), count);
            }

        } catch (Exception ex) {

        }
        try {
            RegistryObject<Item> item = RegistryObject.create(location, ForgeRegistries.ITEMS);
            if (item.isPresent()) {
                return new ItemStack(item.get().asItem(), count);
            }

        } catch (Exception ex) {

        }

        try {
            RegistryObject<Block> item = RegistryObject.create(resourceLocation, ForgeRegistries.BLOCKS);
            if (item.isPresent()) {
                return new ItemStack(item.get().asItem(), count);
            }

        } catch (Exception ex) {

        }

        try {
            RegistryObject<Block> item = RegistryObject.create(location, ForgeRegistries.BLOCKS);
            if (item.isPresent()) {
                return new ItemStack(item.get().asItem(), count);
            }

        } catch (Exception ex) {

        }

        return ItemStack.EMPTY;
    }


    public ResourceLocation GetResourceLocation(String resourceLocation ) {
        if (resourceLocation.contains(":")) {
            String[] parts = resourceLocation.split(":");
            return new ResourceLocation(parts[0], parts[1]);
        } else {
            return new ResourceLocation(resourceLocation);
        }
    }








    public static class FlatpackBuilder extends FlatpackBlockBuilder {
        public FlatpackBuilder(ResourceLocation i) {
            super(i);
            model("kubejs:block/flatpack");
            addBox(3,0,3,10,10,10, false);
        }

        public Block createObject() {
            return new FlatpackBlockJS(this);
        }
    }
}