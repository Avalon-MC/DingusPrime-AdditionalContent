package net.petercashel.dingusprimeacm.shopkeeper.entity.subtypes;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.npc.VillagerType;
import net.minecraft.world.level.Level;
import net.petercashel.dingusprimeacm.shopkeeper.entity.ShopKeeper;
import net.petercashel.dingusprimeacm.shopkeeper.registry.ShopTradeInfo;

public class ShopKeeper_weapons extends ShopKeeper {
    public ShopKeeper_weapons(EntityType<? extends Villager> entityEntityType, Level level) {
        super(entityEntityType, level);
        this.shopType = ShopTradeInfo.ShopType.weapons;
    }

    public ShopKeeper_weapons(EntityType<? extends Villager> entityEntityType, Level level, VillagerType villagerType) {
        super(entityEntityType, level, villagerType);
        this.shopType = ShopTradeInfo.ShopType.weapons;
    }
}
