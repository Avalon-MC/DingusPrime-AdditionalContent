package net.petercashel.dingusprimeacm.shopkeeper.registry;

import joptsimple.util.KeyValuePair;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.item.trading.MerchantOffers;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.registries.ForgeRegistries;
import net.neoforged.registries.RegistryObject;
import net.petercashel.dingusprimeacm.configuration.DPAcmConfig;
import net.petercashel.dingusprimeacm.kubejs.dingusprimeKubeJSPlugin;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

public class ShopTradeManager {

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

        if (total > DPAcmConfig.ConfigInstance.ShopSettings.RandomShopTradesCount) {
            for (int i = 0; i < DPAcmConfig.ConfigInstance.ShopSettings.RandomShopTradesCount; i++) {
                ShopTradeInfo tradeInfo = (ShopTradeInfo) trades.get(random.nextInt(0, total));

                while (TradeAlreadyPicked(tradeInfo, pickedTrades)) {
                    //Need better random
                    int index = random.nextInt(0, (total * 2) -1) / 2;
                    if (index >= total) index = total - 1;

                    tradeInfo = (ShopTradeInfo) trades.get(index);
                }

                pickedTrades.add(tradeInfo);
            }

            pickedTrades.sort(ShopTradeInfoComparater.instance);

            CurrentTrades(_shopType).addAll(pickedTrades);
        } else {
            //Add all
            trades.sort(ShopTradeInfoComparater.instance);
            CurrentTrades(_shopType).addAll((trades));
        }

    }

    private boolean TradeAlreadyPicked(ShopTradeInfo tradeInfo, ArrayList<ShopTradeInfo> pickedTrades) {
        if (pickedTrades.size() == 0) {
            return false;
        }
        boolean picked = false;

        for (var trade: pickedTrades) {
            if (trade.ResultName.toString().equals(tradeInfo.ResultName.toString())) {
                picked = true;
                break;
            }
        }

        return picked;
    }


    public void AddAlwaysTrades(ShopTradeInfo.ShopType _shopType)
    {
        List<ShopTradeInfo> trades = new ArrayList<>();
        for (ShopTradeInfo x : dingusprimeKubeJSPlugin.SHOPTRADE_REGISTRY.get().getValues()) {
            if (x.shopType == _shopType && x.AlwaysAvailible == true) {
                trades.add(x);
            }
        }

        trades.sort(ShopTradeInfoComparater.instance);

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

    public void ResetAll() {
        ResetOffers();
    }
}
