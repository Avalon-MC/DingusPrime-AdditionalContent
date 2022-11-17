package net.petercashel.dingusprimeacm.shopkeeper.entity.subtypes;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.npc.VillagerType;
import net.minecraft.world.level.Level;
import net.petercashel.dingusprimeacm.shopkeeper.entity.ShopKeeper;
import net.petercashel.dingusprimeacm.shopkeeper.registry.ShopTradeInfo;

public class ShopKeeper_custom3 extends ShopKeeper {
    public ShopKeeper_custom3(EntityType<? extends Villager> entityEntityType, Level level) {
        super(entityEntityType, level);
        this.shopType = ShopTradeInfo.ShopType.custom3;
    }

    public ShopKeeper_custom3(EntityType<? extends Villager> entityEntityType, Level level, VillagerType villagerType) {
        super(entityEntityType, level, villagerType);
        this.shopType = ShopTradeInfo.ShopType.custom3;
    }
}
