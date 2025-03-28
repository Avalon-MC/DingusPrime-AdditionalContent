package net.petercashel.dingusprimeacm.kubejs.types.flatpack;

import dev.latvian.mods.kubejs.block.custom.BasicBlockJS;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
    public InteractionResult useWithoutItem(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, BlockHitResult pHit) {
        if (!pLevel.isClientSide && pPlayer.getUsedItemHand() == InteractionHand.MAIN_HAND)
        {
            for (var ItemData: ItemsToCreate) {
                ResourceLocation location = GetResourceLocation(ItemData.ResourceName);
                ItemStack stack = GetItemStack(location, ItemData.Amount);

                if (stack != null && stack.isEmpty() == false) {
                    pLevel.addFreshEntity(new ItemEntity(pPlayer.level(), pPlayer.position().x, pPlayer.position().y, pPlayer.position().z, stack));
                }
            }

            pLevel.destroyBlock(pPos, false);

            return InteractionResult.SUCCESS;
        }


        return super.useWithoutItem(pState, pLevel, pPos, pPlayer, pHit);
    }

    public ItemStack CreateItem(int i) {
        if (i+1 > ItemsToCreate.size()) {
            return ItemStack.EMPTY;
        }
        return GetItemStack(ResourceLocation.parse(ItemsToCreate.get(i).ResourceName), ItemsToCreate.get(i).Amount);
    }


    public List<FormattedCharSequence> GetAllItemNames() {
        List<FormattedCharSequence> names = new ArrayList<>();

        for (var item : ItemsToCreate ) {
            ItemStack stack = GetItemStack(ResourceLocation.parse(item.ResourceName), item.Amount);
            names.add(Component.literal(item.Amount + "x ").append(stack.getHoverName()).append("").getVisualOrderText());
        }



        return names;
    }

    private ItemStack GetItemStack(ResourceLocation resourceLocation, int count) {

        ResourceLocation location = ResourceLocation.fromNamespaceAndPath("kubejs", resourceLocation.getPath());

        try {
            Optional<Holder.Reference<Item>> item = BuiltInRegistries.ITEM.getHolder(resourceLocation);
            if (item.isPresent()) {
                return new ItemStack(item.get().value().asItem(), count);
            }

        } catch (Exception ex) {

        }

        try {
            Optional<Holder.Reference<Item>> item = BuiltInRegistries.ITEM.getHolder(location);
            if (item.isPresent()) {
                return new ItemStack(item.get().value().asItem(), count);
            }

        } catch (Exception ex) {

        }

        try {
            Optional<Holder.Reference<Block>> item = BuiltInRegistries.BLOCK.getHolder(resourceLocation);
            if (item.isPresent()) {
                return new ItemStack(item.get().value().asItem(), count);
            }

        } catch (Exception ex) {

        }

        try {
            Optional<Holder.Reference<Block>> item = BuiltInRegistries.BLOCK.getHolder(location);
            if (item.isPresent()) {
                return new ItemStack(item.get().value().asItem(), count);
            }

        } catch (Exception ex) {

        }

        return ItemStack.EMPTY;
    }


    public ResourceLocation GetResourceLocation(String resourceLocation ) {
        if (resourceLocation.contains(":")) {
            String[] parts = resourceLocation.split(":");
            return ResourceLocation.fromNamespaceAndPath(parts[0], parts[1]);
        } else {
            return ResourceLocation.parse(resourceLocation);
        }
    }








    public static class FlatpackBuilder extends FlatpackBlockBuilder {
        public FlatpackBuilder(ResourceLocation i) {
            super(i);
            parentModel(ResourceLocation.parse("kubejs:block/flatpack"));
            addBox_int(3,0,3,10,10,10, false);
        }

        public Block createObject() {
            return new FlatpackBlockJS(this);
        }
    }
}
