package net.petercashel.dingusprimeacm.shopkeeper.registry;

import joptsimple.util.KeyValuePair;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.item.trading.MerchantOffers;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.petercashel.dingusprimeacm.kubejs.dingusprimeKubeJSPlugin;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

public class ShopTradeManager {

    private final int RandomTradesCount = 3;
    private final Random random = new Random();
    public static final ShopTradeManager INSTANCE = new ShopTradeManager();
    private long savedMillis;
    public ConcurrentHashMap<ShopTradeInfo.ShopType, ArrayList<ShopTradeInfo>> CurrentTradeDict = new  ConcurrentHashMap<>();
    public ArrayList<ShopTradeInfo> CurrentTrades(ShopTradeInfo.ShopType key) {
        if (CurrentTradeDict.containsKey(key)) return CurrentTradeDict.get(key);
        else { CurrentTradeDict.put(key, new ArrayList<>()); return CurrentTradeDict.get(key); }
    };

    public ShopTradeManager() {
        // Save current time of construction
        savedMillis = System.currentTimeMillis();
    }

    private void ResetOffers() {
        CurrentTradeDict.clear();
    }

    public void UpdateRandomTrades(ShopTradeInfo.ShopType _shopType) {
        CurrentTrades(_shopType).clear();
        ArrayList<ShopTradeInfo> pickedTrades = new ArrayList<>();

        List<ShopTradeInfo> trades = new ArrayList<>();
        for (ShopTradeInfo x : dingusprimeKubeJSPlugin.SHOPTRADE_REGISTRY.get().getValues()) {
            if (x.shopType == _shopType && x.AlwaysAvailible == false) {
                trades.add(x);
            }
        }
        int total = (int) trades.size();

        if (total > 3) {
            for (int i = 0; i < RandomTradesCount; i++) {
                ShopTradeInfo tradeInfo = (ShopTradeInfo) trades.get(random.nextInt(0, total) - 1);

                if (pickedTrades.contains(tradeInfo)) {
                    tradeInfo = (ShopTradeInfo) trades.get(random.nextInt(0, total) - 1);
                }
                if (pickedTrades.contains(tradeInfo)) {
                    tradeInfo = (ShopTradeInfo) trades.get(random.nextInt(0, total) - 1);
                }

                CurrentTrades(_shopType).add(tradeInfo);
                pickedTrades.add(tradeInfo);
            }
        } else {
            //Add all
            CurrentTrades(_shopType).addAll((trades));
        }


    }


    public void AddAlwaysTrades(ShopTradeInfo.ShopType _shopType)
    {
        List<ShopTradeInfo> trades = new ArrayList<>();
        for (ShopTradeInfo x : dingusprimeKubeJSPlugin.SHOPTRADE_REGISTRY.get().getValues()) {
            if (x.shopType == _shopType && x.AlwaysAvailible == true) {
                trades.add(x);
            }
        }
        CurrentTrades(_shopType).addAll((trades));
    }

    public void ApplyOffers(MerchantOffers pOffers, ShopTradeInfo.ShopType _shopType) {

        // Check time elapsed
        if (System.currentTimeMillis() >= savedMillis + 24 * 60 * 60 * 1000) {
            ResetOffers();
        }

        if (CurrentTrades(_shopType).isEmpty()) {
            UpdateRandomTrades(_shopType);
            AddAlwaysTrades(_shopType);
        }

        pOffers.clear();

        if (!CurrentTrades(_shopType).isEmpty()) {
            for (ShopTradeInfo info: CurrentTrades(_shopType)) {
                ItemStack stack = TryGetStackFromName(info.ResultName, info.Count, info.shopResultType);
                if (!stack.isEmpty()) {
                    pOffers.add(new MerchantOffer(ItemStack.EMPTY, ItemStack.EMPTY, stack, 99, 1, info.Cost));
                }
            }
        }

        if (pOffers.isEmpty()) {
            //Fallback testing trade
            pOffers.add(new MerchantOffer(ItemStack.EMPTY, ItemStack.EMPTY, new ItemStack(Blocks.COBBLESTONE.asItem()), 99, 1, 1));
        }

    }

    public static ItemStack TryGetStackFromName(ResourceLocation resourceLocation, int count, ShopTradeInfo.ShopResultType shopResultType) {
        if (shopResultType == ShopTradeInfo.ShopResultType.block) {
            try {
                RegistryObject<Block> item = RegistryObject.create(resourceLocation, ForgeRegistries.BLOCKS);
                if (item.isPresent()) {
                    return new ItemStack(item.get().asItem(), count);
                }

            } catch (Exception ex) {

            }
        } else {
            try {
                RegistryObject<Item> item = RegistryObject.create(resourceLocation, ForgeRegistries.ITEMS);
                if (item.isPresent()) {
                    return new ItemStack(item.get(), count);
                }

            } catch (Exception ex) {

            }
        }

        ResourceLocation location = new ResourceLocation("kubejs", resourceLocation.getPath());

        if (shopResultType == ShopTradeInfo.ShopResultType.block) {
            try {
                RegistryObject<Block> item = RegistryObject.create(location, ForgeRegistries.BLOCKS);
                if (item.isPresent()) {
                    return new ItemStack(item.get().asItem(), count);
                }

            } catch (Exception ex) {

            }
        } else {
            try {
                RegistryObject<Item> item = RegistryObject.create(location, ForgeRegistries.ITEMS);
                if (item.isPresent()) {
                    return new ItemStack(item.get(), count);
                }

            } catch (Exception ex) {

            }
        }

        return ItemStack.EMPTY;
    }
}
