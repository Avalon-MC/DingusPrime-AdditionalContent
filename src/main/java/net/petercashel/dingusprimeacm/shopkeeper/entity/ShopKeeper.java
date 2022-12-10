package net.petercashel.dingusprimeacm.shopkeeper.entity;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Dynamic;
import net.minecraft.Util;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.stats.Stats;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.behavior.VillagerGoalPackages;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.ai.sensing.Sensor;
import net.minecraft.world.entity.ai.sensing.SensorType;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.entity.npc.VillagerType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.schedule.Activity;
import net.minecraft.world.entity.schedule.Schedule;
import net.minecraft.world.inventory.MerchantMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.item.trading.MerchantOffers;
import net.minecraft.world.level.Level;
import net.petercashel.dingusprimeacm.networking.PacketHandler;
import net.petercashel.dingusprimeacm.networking.packets.ShopkeeperMerchantOffersPacket_SC;
import net.petercashel.dingusprimeacm.shopkeeper.container.ShopKeeperMenu;
import net.petercashel.dingusprimeacm.shopkeeper.registry.ShopTradeInfo;
import net.petercashel.dingusprimeacm.shopkeeper.registry.ShopTradeManager;
import org.jetbrains.annotations.Nullable;

import java.util.OptionalInt;

public class ShopKeeper extends Villager {

    private static final ImmutableList<MemoryModuleType<?>> MEMORY_TYPES = ImmutableList.of(MemoryModuleType.HOME, MemoryModuleType.JOB_SITE, MemoryModuleType.POTENTIAL_JOB_SITE, MemoryModuleType.MEETING_POINT, MemoryModuleType.NEAREST_LIVING_ENTITIES, MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES, MemoryModuleType.VISIBLE_VILLAGER_BABIES, MemoryModuleType.NEAREST_PLAYERS, MemoryModuleType.NEAREST_VISIBLE_PLAYER, MemoryModuleType.NEAREST_VISIBLE_ATTACKABLE_PLAYER, MemoryModuleType.NEAREST_VISIBLE_WANTED_ITEM, MemoryModuleType.WALK_TARGET, MemoryModuleType.LOOK_TARGET, MemoryModuleType.INTERACTION_TARGET, MemoryModuleType.BREED_TARGET, MemoryModuleType.PATH, MemoryModuleType.DOORS_TO_CLOSE, MemoryModuleType.NEAREST_BED, MemoryModuleType.HURT_BY, MemoryModuleType.HURT_BY_ENTITY, MemoryModuleType.NEAREST_HOSTILE, MemoryModuleType.SECONDARY_JOB_SITE, MemoryModuleType.HIDING_PLACE, MemoryModuleType.HEARD_BELL_TIME, MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE, MemoryModuleType.LAST_SLEPT, MemoryModuleType.LAST_WOKEN, MemoryModuleType.LAST_WORKED_AT_POI, MemoryModuleType.GOLEM_DETECTED_RECENTLY);
    private static final ImmutableList<SensorType<? extends Sensor<? super Villager>>> SENSOR_TYPES = ImmutableList.of(SensorType.NEAREST_LIVING_ENTITIES, SensorType.NEAREST_PLAYERS, SensorType.NEAREST_ITEMS, SensorType.NEAREST_BED, SensorType.HURT_BY, SensorType.VILLAGER_HOSTILES, SensorType.VILLAGER_BABIES, SensorType.SECONDARY_POIS, SensorType.GOLEM_DETECTED);


    public ShopTradeInfo.ShopType shopType = ShopTradeInfo.ShopType.furniture;

    public ShopKeeper(EntityType<? extends Villager> entityEntityType, Level level) {
        this(entityEntityType, level, VillagerType.PLAINS);
    }

    public ShopKeeper(EntityType<? extends Villager> entityEntityType, Level level, VillagerType villagerType) {
        super(entityEntityType, level, villagerType);

        ((GroundPathNavigation)this.getNavigation()).setCanOpenDoors(false);
        this.getNavigation().setCanFloat(false);
        this.setCanPickUpLoot(false);
        ((GroundPathNavigation) this.getNavigation()).setCanPassDoors(false);

    }



    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes().add(Attributes.MOVEMENT_SPEED, 0.5D).add(Attributes.FOLLOW_RANGE, 48.0D);
    }


    @Override
    protected Brain.Provider<Villager> brainProvider() {
        return Brain.provider(MEMORY_TYPES, SENSOR_TYPES);
    }

    @Override
    protected Brain<?> makeBrain(Dynamic<?> pDynamic) {
        Brain<Villager> brain = this.brainProvider().makeBrain(pDynamic);
        this.registerBrainGoals(brain);
        return brain;
    }

    @Override
    public void refreshBrain(ServerLevel pServerLevel) {
        Brain<Villager> brain = this.getBrain();
        brain.stopAll(pServerLevel, this);
        this.brain = brain.copyWithoutBehaviors();
        this.registerBrainGoals(this.getBrain());
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();

        this.goalSelector.addGoal(1, new TradeWithPlayerGoal(this));
        this.goalSelector.addGoal(1, new LookAtTradingPlayerGoal(this));
        this.goalSelector.addGoal(9, new InteractGoal(this, Player.class, 3.0F, 1.0F));
        this.goalSelector.addGoal(10, new LookAtPlayerGoal(this, Mob.class, 8.0F));

    }

    private void registerBrainGoals(Brain<Villager> pVillagerBrain) {
        VillagerProfession villagerprofession = this.getVillagerData().getProfession();
//        {
//            pVillagerBrain.setSchedule(Schedule.VILLAGER_DEFAULT);
//            pVillagerBrain.addActivityWithConditions(Activity.WORK, VillagerGoalPackages.getWorkPackage(villagerprofession, 0.5F), ImmutableSet.of(Pair.of(MemoryModuleType.JOB_SITE, MemoryStatus.VALUE_PRESENT)));
//        }

        //pVillagerBrain.addActivity(Activity.CORE, VillagerGoalPackages.getCorePackage(villagerprofession, 0.5F));
        //pVillagerBrain.addActivityWithConditions(Activity.MEET, VillagerGoalPackages.getMeetPackage(villagerprofession, 0.5F), ImmutableSet.of(Pair.of(MemoryModuleType.MEETING_POINT, MemoryStatus.VALUE_PRESENT)));
        //pVillagerBrain.addActivity(Activity.REST, VillagerGoalPackages.getRestPackage(villagerprofession, 0.5F));
        pVillagerBrain.addActivity(Activity.IDLE, VillagerGoalPackages.getIdlePackage(villagerprofession, 0.5F));
        //pVillagerBrain.addActivity(Activity.PANIC, VillagerGoalPackages.getPanicPackage(villagerprofession, 0.5F));
        //pVillagerBrain.addActivity(Activity.PRE_RAID, VillagerGoalPackages.getPreRaidPackage(villagerprofession, 0.5F));
        //pVillagerBrain.addActivity(Activity.RAID, VillagerGoalPackages.getRaidPackage(villagerprofession, 0.5F));
        //pVillagerBrain.addActivity(Activity.HIDE, VillagerGoalPackages.getHidePackage(villagerprofession, 0.5F));



        //pVillagerBrain.setCoreActivities(ImmutableSet.of(Activity.CORE));
        pVillagerBrain.setCoreActivities(ImmutableSet.of(Activity.IDLE));
        pVillagerBrain.setDefaultActivity(Activity.IDLE);
        pVillagerBrain.setActiveActivityIfPossible(Activity.IDLE);
        //pVillagerBrain.updateActivityFromSchedule(this.level.getDayTime(), this.level.getGameTime());
    }

    @Override
    protected void updateTrades() {
        //super.updateTrades();

        MerchantOffers merchantoffers = this.getOffers();
        overrideOffers(merchantoffers);

    }


    @Override
    public void notifyTradeUpdated(ItemStack pStack) {
        super.notifyTradeUpdated(pStack);
    }

    @Override
    public void notifyTrade(MerchantOffer pOffer) {
        super.notifyTrade(pOffer);

        //Infinite Uses
        //pOffer.resetUses();
    }

    @Override
    public MerchantOffers getOffers() {
        //Update Offers handles this

        //Force
        MerchantOffers merchantoffers = super.getOffers();
        overrideOffers(merchantoffers);

        return merchantoffers;
    }

    @Override
    public void overrideOffers(@Nullable MerchantOffers pOffers) {
        //Override offers here?
        pOffers.clear();

        //Dummy offer.
        pOffers.add(new MerchantOffer(ItemStack.EMPTY, ItemStack.EMPTY, new ItemStack(Items.WHITE_WOOL), 99, 1, 1.0f));
        pOffers.add(new MerchantOffer(ItemStack.EMPTY, ItemStack.EMPTY, new ItemStack(Items.RED_WOOL), 99, 1, 1.0f));
        pOffers.add(new MerchantOffer(ItemStack.EMPTY, ItemStack.EMPTY, new ItemStack(Items.BLUE_WOOL), 99, 1, 1.0f));
        pOffers.add(new MerchantOffer(ItemStack.EMPTY, ItemStack.EMPTY, new ItemStack(Items.BLACK_WOOL), 99, 1, 15.0f));


        if (!this.level.isClientSide()) {
            ShopTradeManager.INSTANCE.ApplyOffers(pOffers, shopType);
        }
    }

    @Override
    protected void addOffersFromItemListings(MerchantOffers pGivenMerchantOffers, VillagerTrades.ItemListing[] pNewTrades, int pMaxNumbers) {
        super.addOffersFromItemListings(pGivenMerchantOffers, pNewTrades, pMaxNumbers);
    }

    @Override
    public void setOffers(MerchantOffers pOffers) {
        super.setOffers(pOffers);
    }


    @Override
    public InteractionResult mobInteract(Player pPlayer, InteractionHand pHand) {
        if (pHand != InteractionHand.MAIN_HAND) { //Dont double execute
            return super.mobInteract(pPlayer, pHand);
        }

        ItemStack itemstack = pPlayer.getItemInHand(pHand);
        if (itemstack.getItem() != Items.VILLAGER_SPAWN_EGG && this.isAlive() && !this.isTrading() && !this.isSleeping() && !pPlayer.isSecondaryUseActive()) {
            {
                boolean flag = this.getOffers().isEmpty();
                if (pHand == InteractionHand.MAIN_HAND) {
                    if (flag && !this.level.isClientSide) {
                        this.setUnhappy();
                    }

                    pPlayer.awardStat(Stats.TALKED_TO_VILLAGER);
                }

                if (flag) {
                    return InteractionResult.sidedSuccess(this.level.isClientSide);
                } else {
                    if (!this.level.isClientSide && !this.offers.isEmpty()) {
                        this.setTradingPlayer(pPlayer);
                        this.startTrading(pPlayer);
                    }

                    return InteractionResult.sidedSuccess(this.level.isClientSide);
                }
            }
        } else {
            return super.mobInteract(pPlayer, pHand);
        }
    }

    private void setUnhappy() {
//        this.setUnhappyCounter(40);
//        if (!this.level.isClientSide()) {
//            this.playSound(SoundEvents.VILLAGER_NO, this.getSoundVolume(), this.getVoicePitch());
//        }

    }

    private void startTrading(Player pPlayer) {
        this.setTradingPlayer(pPlayer);
        this.openTradingScreen(pPlayer, this.getDisplayName(), this.getVillagerData().getLevel());
    }

    @Override
    public void openTradingScreen(Player pPlayer, Component pDisplayName, int pLevel) {
        OptionalInt optionalint = pPlayer.openMenu(new SimpleMenuProvider((p_45298_, p_45299_, p_45300_) -> {
            return new ShopKeeperMenu(p_45298_, p_45299_, this);
        }, pDisplayName));
        if (optionalint.isPresent()) {
            MerchantOffers merchantoffers = this.getOffers();

            //Force Update
            overrideOffers(merchantoffers);

            if (!merchantoffers.isEmpty()) {
                ShopkeeperMerchantOffersPacket_SC packet = new ShopkeeperMerchantOffersPacket_SC(optionalint.getAsInt(), merchantoffers, pLevel, this.getVillagerXp(), this.showProgressBar(), this.canRestock());
                PacketHandler.sendToPlayer(packet, (ServerPlayer) pPlayer);

            } else {
                System.out.println("DUMMY OFFERES MISSING");
            }
        }
    }

    @Override
    public boolean hurt(DamageSource pSource, float pAmount) {
        //Always hurt zombies
        if (pSource.getEntity() != null && pSource.getEntity() instanceof Zombie) {
            pSource.getEntity().hurt(pSource, pAmount); //Pass it on
        }
        if (pSource.isCreativePlayer()) {
            //Always by creative players
            if (((Player)(pSource.getEntity())).isCrouching()) {
                return super.hurt(pSource, pAmount * 100);
            } else {
                ((Player)(pSource.getEntity())).sendMessage(new TextComponent("Shift Punch for Max Removal"), Util.NIL_UUID);
            }


        }

        //Redirect to attacker
        if (pSource.getEntity() != null) {
            pSource.getEntity().hurt(pSource, pAmount); //Pass it on
        }

        return false; //Not Hurt
    }
}
