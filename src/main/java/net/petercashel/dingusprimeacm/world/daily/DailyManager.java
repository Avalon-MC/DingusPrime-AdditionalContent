package net.petercashel.dingusprimeacm.world.daily;

import net.minecraft.Util;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.petercashel.dingusprimeacm.configuration.DPAcmConfig;
import net.petercashel.dingusprimeacm.dingusprimeacm;
import net.petercashel.dingusprimeacm.shopkeeper.container.ShopkeeperCurrencyHelper;
import net.petercashel.dingusprimeacm.world.WorldDataManager;
import net.petercashel.dingusprimeacm.world.zones.selection.PlayerSelectionSession;

import java.util.Map;

import static net.petercashel.dingusprimeacm.world.zones.ZoneManager.Instance;

public class DailyManager {
    public static DailyManager Instance = new DailyManager();
    public DailyData Data = new DailyData();


    @Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE, modid = dingusprimeacm.MODID)
    public class DailyManagerEvents {
        @SubscribeEvent
        public static void OnPlayerLoggedInEvent(PlayerEvent.PlayerLoggedInEvent event) {
            if (event.getPlayer().getLevel().isClientSide) return;
            Instance.HandleDailyLogin((ServerPlayer) event.getPlayer());
        }
}

    public static void HandleDailyLogin(ServerPlayer player) {
        if (!DPAcmConfig.ConfigInstance.DailyRewardSettings.DailyRewardsEnabled) return;
        var data = Instance.Data.GetPlayerDailyRewardData(player);

        if (data.hasReward()) {
            var Reward = data.GetCurrentReward();
            if (Reward == DailyRewardData.DailyRewardStatus.FirstTime) {
                int currency = GetReward(0);
                player.sendMessage(Component.literal("First Time Login Bonus! ").append(ShopkeeperCurrencyHelper.formatCurrency(currency)), Util.NIL_UUID);
                ShopkeeperCurrencyHelper.refundPlayer(player, currency);
            }
            else if (Reward == DailyRewardData.DailyRewardStatus.TooEarly) {

            }
            else if (Reward == DailyRewardData.DailyRewardStatus.TooLate) {
                //Reset and to day 0 and call again.
                data.currentRewardLevel = 0;
                data.nextTimestamp = 0;
                data.MarkDirty();

                var rewardlevel = data.GetRewardLevel();
                int currency = GetReward(rewardlevel);
                player.sendMessage(Component.literal("Daily Login Bonus Missed! ").append(ShopkeeperCurrencyHelper.formatCurrency(currency)), Util.NIL_UUID);
            }
            else {
                var rewardlevel = data.GetRewardLevel();
                int currency = GetReward(rewardlevel);
                if (rewardlevel == GetMaxDailyRewardLevel()) {
                    player.sendMessage(Component.literal("Daily Login Bonus Maxed! ").append(ShopkeeperCurrencyHelper.formatCurrency(currency)), Util.NIL_UUID);
                    ShopkeeperCurrencyHelper.refundPlayer(player, currency);
                } else {
                    if (rewardlevel > 1) {
                        //Streak?
                        player.sendMessage(Component.literal("Daily Login Bonus! ").append(ShopkeeperCurrencyHelper.formatCurrency(currency)), Util.NIL_UUID);
                        ShopkeeperCurrencyHelper.refundPlayer(player, currency);
                    } else {
                        player.sendMessage(Component.literal("Daily Login Bonus! ").append(ShopkeeperCurrencyHelper.formatCurrency(currency)), Util.NIL_UUID);
                        ShopkeeperCurrencyHelper.refundPlayer(player, currency);
                    }
                }
            }
            Instance.Data.MarkDirty();
        }
    }

    private static int GetReward(int i) {
        if (Instance.Data.DailyRewardMap.isEmpty()) return 0;
        if (Instance.Data.DailyRewardMap.containsKey(i)) {
            return Instance.Data.DailyRewardMap.get(i);
        }
        int lastKey = 0;
        for (int key : Instance.Data.DailyRewardMap.keySet()) {
            if (key < i) {
                lastKey = key; //Keep searching
            } else {
                break; //lastKey is now the last registered reward before the number
            }
        }

        if (Instance.Data.DailyRewardMap.containsKey(lastKey)) {
            return Instance.Data.DailyRewardMap.get(lastKey);
        }

        return 0; //Failed
    }

    public static int GetMaxDailyRewardLevel() {
        if (Instance.Data.DailyRewardMap.isEmpty()) return 0;
        return Instance.Data.DailyRewardMap.lastKey();
    }

    public static void LoadDailyRewards() {
        //DailyRewardMap
        Instance.Data.DailyRewardMap.putAll(DPAcmConfig.ConfigInstance.DailyRewardSettings.DailyRewardsCurrency);


    }

    public void MarkDirty() {
        WorldDataManager.SaveDataInstance.markDirty();
    }


}
