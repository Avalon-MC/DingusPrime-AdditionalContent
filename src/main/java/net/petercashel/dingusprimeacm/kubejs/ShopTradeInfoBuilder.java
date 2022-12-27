package net.petercashel.dingusprimeacm.kubejs;

import dev.latvian.mods.kubejs.BuilderBase;
import dev.latvian.mods.kubejs.RegistryObjectBuilderTypes;
import net.minecraft.resources.ResourceLocation;
import net.petercashel.dingusprimeacm.shopkeeper.registry.ShopTradeInfo;

public class ShopTradeInfoBuilder extends BuilderBase<ShopTradeInfo> {


    public ResourceLocation ResultName = null;
    public int Cost = 1;
    public int Count = 1;
    public boolean AlwaysAvailible = false;
    public ShopTradeInfo.ShopType shopType = ShopTradeInfo.ShopType.furniture;
    public ShopTradeInfo.ShopResultType shopResultType = ShopTradeInfo.ShopResultType.block;

    public ShopTradeInfoBuilder(ResourceLocation i) {
        super(i);
    }

    @Override
    public RegistryObjectBuilderTypes<ShopTradeInfo> getRegistryType() {
        return dingusprimeKubeJSPlugin.SHOPTRADE;
    }

    @Override
    public ShopTradeInfo createObject() {
        return new ShopTradeInfo(this);
    }

    public ShopTradeInfoBuilder result(String resourceLocation ) {
        if (resourceLocation.contains(":")) {
            String[] parts = resourceLocation.split(":");
            this.ResultName = new ResourceLocation(parts[0], parts[1]);
        } else {
            this.ResultName = new ResourceLocation(resourceLocation);
        }
        return this;
    }

    public ShopTradeInfoBuilder cost(int cost) {
        this.Cost = cost;
        return this;
    }

    public ShopTradeInfoBuilder count(int count) {
        this.Count = count;
        return this;
    }

    public ShopTradeInfoBuilder always() {
        this.AlwaysAvailible = true;
        return this;
    }

    public ShopTradeInfoBuilder shopType(ShopTradeInfo.ShopType shopType) {
        this.shopType = shopType;
        return this;
    }

    public ShopTradeInfoBuilder shopResultType(ShopTradeInfo.ShopResultType shopResultType) {
        this.shopResultType = shopResultType;
        return this;
    }

}
