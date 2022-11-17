package net.petercashel.dingusprimeacm.shopkeeper.entity.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.VillagerModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.CrossedArmsItemLayer;
import net.minecraft.client.renderer.entity.layers.CustomHeadLayer;
import net.minecraft.client.renderer.entity.layers.VillagerProfessionLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.npc.Villager;
import net.petercashel.dingusprimeacm.dingusprimeacm;
import net.petercashel.dingusprimeacm.shopkeeper.entity.ShopKeeper;

public class ShopKeeperRenderer extends MobRenderer<ShopKeeper, VillagerModel<ShopKeeper>> {

    private static final ResourceLocation SHOPKEEPER_SKIN_furniture = new ResourceLocation(dingusprimeacm.MODID,"textures/entity/villager/shopkeeper_furniture.png");
    private static final ResourceLocation SHOPKEEPER_SKIN_general = new ResourceLocation(dingusprimeacm.MODID,"textures/entity/villager/shopkeeper_general.png");

    private static final ResourceLocation SHOPKEEPER_SKIN_weapons = new ResourceLocation(dingusprimeacm.MODID,"textures/entity/villager/shopkeeper_weapons.png");
    private static final ResourceLocation SHOPKEEPER_SKIN_armor = new ResourceLocation(dingusprimeacm.MODID,"textures/entity/villager/shopkeeper_armor.png");
    private static final ResourceLocation SHOPKEEPER_SKIN_tools = new ResourceLocation(dingusprimeacm.MODID,"textures/entity/villager/shopkeeper_tools.png");

    private static final ResourceLocation SHOPKEEPER_SKIN_seeds = new ResourceLocation(dingusprimeacm.MODID,"textures/entity/villager/shopkeeper_seeds.png");
    private static final ResourceLocation SHOPKEEPER_SKIN_trees = new ResourceLocation(dingusprimeacm.MODID,"textures/entity/villager/shopkeeper_trees.png");
    private static final ResourceLocation SHOPKEEPER_SKIN_plants = new ResourceLocation(dingusprimeacm.MODID,"textures/entity/villager/shopkeeper_plants.png");

    private static final ResourceLocation SHOPKEEPER_SKIN_cosmetic = new ResourceLocation(dingusprimeacm.MODID,"textures/entity/villager/shopkeeper_cosmetic.png");
    private static final ResourceLocation SHOPKEEPER_SKIN_hats = new ResourceLocation(dingusprimeacm.MODID,"textures/entity/villager/shopkeeper_hats.png");
    private static final ResourceLocation SHOPKEEPER_SKIN_shirts = new ResourceLocation(dingusprimeacm.MODID,"textures/entity/villager/shopkeeper_shirts.png");
    private static final ResourceLocation SHOPKEEPER_SKIN_pants = new ResourceLocation(dingusprimeacm.MODID,"textures/entity/villager/shopkeeper_pants.png");
    private static final ResourceLocation SHOPKEEPER_SKIN_shoes = new ResourceLocation(dingusprimeacm.MODID,"textures/entity/villager/shopkeeper_shoes.png");

    private static final ResourceLocation SHOPKEEPER_SKIN_curios = new ResourceLocation(dingusprimeacm.MODID,"textures/entity/villager/shopkeeper_curios.png");

    private static final ResourceLocation SHOPKEEPER_SKIN_custom1 = new ResourceLocation(dingusprimeacm.MODID,"textures/entity/villager/shopkeeper_custom1.png");
    private static final ResourceLocation SHOPKEEPER_SKIN_custom2 = new ResourceLocation(dingusprimeacm.MODID,"textures/entity/villager/shopkeeper_custom2.png");
    private static final ResourceLocation SHOPKEEPER_SKIN_custom3 = new ResourceLocation(dingusprimeacm.MODID,"textures/entity/villager/shopkeeper_custom3.png");
    private static final ResourceLocation SHOPKEEPER_SKIN_custom4 = new ResourceLocation(dingusprimeacm.MODID,"textures/entity/villager/shopkeeper_custom4.png");



    @Override
    public ResourceLocation getTextureLocation(ShopKeeper pEntity) {
        switch (pEntity.shopType) {

            case furniture -> {
                return SHOPKEEPER_SKIN_furniture;
            }
            case general -> {
                return SHOPKEEPER_SKIN_general;
            }
            case weapons -> {
                return SHOPKEEPER_SKIN_weapons;
            }
            case armor -> {
                return SHOPKEEPER_SKIN_armor;
            }
            case tools -> {
                return SHOPKEEPER_SKIN_tools;
            }
            case seeds -> {
                return SHOPKEEPER_SKIN_seeds;
            }
            case trees -> {
                return SHOPKEEPER_SKIN_trees;
            }
            case plants -> {
                return SHOPKEEPER_SKIN_plants;
            }
            case cosmetic -> {
                return SHOPKEEPER_SKIN_cosmetic;
            }
            case hats -> {
                return SHOPKEEPER_SKIN_hats;
            }
            case shirts -> {
                return SHOPKEEPER_SKIN_shirts;
            }
            case pants -> {
                return SHOPKEEPER_SKIN_pants;
            }
            case shoes -> {
                return SHOPKEEPER_SKIN_shoes;
            }
            case curios -> {
                return SHOPKEEPER_SKIN_curios;
            }
            case custom1 -> {
                return SHOPKEEPER_SKIN_custom1;
            }
            case custom2 -> {
                return SHOPKEEPER_SKIN_custom2;
            }
            case custom3 -> {
                return SHOPKEEPER_SKIN_custom3;
            }
            case custom4 -> {
                return SHOPKEEPER_SKIN_custom4;
            }
        }
        return SHOPKEEPER_SKIN_furniture;
    }

    public ShopKeeperRenderer(EntityRendererProvider.Context p_174437_) {
        super(p_174437_, new VillagerModel<>(p_174437_.bakeLayer(ModelLayers.VILLAGER)), 0.5F);
        this.addLayer(new CustomHeadLayer<>(this, p_174437_.getModelSet()));
        //this.addLayer(new VillagerProfessionLayer<>(this, p_174437_.getResourceManager(), "villager"));
        this.addLayer(new CrossedArmsItemLayer<>(this));
    }

    protected void scale(Villager pLivingEntity, PoseStack pMatrixStack, float pPartialTickTime) {
        float f = 0.9375F;
        if (pLivingEntity.isBaby()) {
            f *= 0.5F;
            this.shadowRadius = 0.25F;
        } else {
            this.shadowRadius = 0.5F;
        }

        pMatrixStack.scale(f, f, f);
    }
}
