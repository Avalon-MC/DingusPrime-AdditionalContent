package net.petercashel.dingusprimeacm.shopkeeper.registry;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistryEntry;
import net.petercashel.dingusprimeacm.kubejs.ShopTradeInfoBuilder;

public class ShopTradeInfo extends ForgeRegistryEntry<ShopTradeInfo> {
    public ShopTradeInfo(ShopTradeInfoBuilder shopTradeInfoBuilder) {
        ResultName = shopTradeInfoBuilder.ResultName;
        Cost = shopTradeInfoBuilder.Cost;
        Count = shopTradeInfoBuilder.Count;
        AlwaysAvailible = shopTradeInfoBuilder.AlwaysAvailible;
        shopType = shopTradeInfoBuilder.shopType;
        shopResultType = shopTradeInfoBuilder.shopResultType;

        this.setRegistryName(shopTradeInfoBuilder.id);
    }

    public ResourceLocation ResultName = null;
    public int Cost = 1;
    public int Count = 1;
    public boolean AlwaysAvailible = false;
    public ShopType shopType = ShopType.furniture;
    public ShopResultType shopResultType = ShopResultType.block;

    public enum ShopResultType
    {
        block,
        item,

        ;

            public static ShopResultType getShopResultTrade(Object o) {
            return ShopResultType.valueOf(o.toString().toLowerCase());
        }
    }

    public enum ShopType {
        furniture,

        general,

        weapons,
        armor,
        tools,

        seeds,
        trees,
        plants,

        cosmetic,
        hats,
        shirts,
        pants,
        shoes,

        curios,

        custom1,
        custom2,
        custom3,
        custom4,
        ;

        public static ShopType getShopTrade(Object o) {
            return ShopType.valueOf(o.toString().toLowerCase());
        }
    }
}
