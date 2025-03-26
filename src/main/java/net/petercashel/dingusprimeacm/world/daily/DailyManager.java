package net.petercashel.dingusprimeacm.world.daily;

import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.petercashel.dingusprimeacm.Config;
import net.petercashel.dingusprimeacm.world.WorldDataManager;

public class DailyManager {
    public static DailyManager Instance = new DailyManager();
    public DailyData Data = new DailyData();


    public class DailyManagerEvents {
        @SubscribeEvent
        public static void OnPlayerLoggedInEvent(PlayerEvent.PlayerLoggedInEvent event) {
            if (event.getEntity().level().isClientSide) return;
            Instance.HandleDailyLogin((ServerPlayer) event.getEntity());
        }
}

    public static void HandleDailyLogin(ServerPlayer player) {
        if (!Config.DailyRewardSettings_Enabled) return;
        var data = Instance.Data.GetPlayerDailyRewardData(player);

        if (data.hasReward()) {
            var Reward = data.GetCurrentReward();
            if (Reward == DailyRewardData.DailyRewardStatus.FirstTime) {
                int currency = GetReward(0);
                //player.sendSystemMessage(Component.literal("First Time Login Bonus! ").append(ShopkeeperCurrencyHelper.formatCurrency(currency)));
                //ShopkeeperCurrencyHelper.refundPlayer(player, currency);
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
                //player.sendSystemMessage(Component.literal("Daily Login Bonus Missed! ").append(ShopkeeperCurrencyHelper.formatCurrency(currency)));
            }
            else {
                var rewardlevel = data.GetRewardLevel();
                int currency = GetReward(rewardlevel);
                if (rewardlevel == GetMaxDailyRewardLevel()) {
                    //player.sendSystemMessage(Component.literal("Daily Login Bonus Maxed! ").append(ShopkeeperCurrencyHelper.formatCurrency(currency)));
                    //ShopkeeperCurrencyHelper.refundPlayer(player, currency);
                } else {
                    if (rewardlevel > 1) {
                        //Streak?
                        //player.sendSystemMessage(Component.literal("Daily Login Bonus! ").append(ShopkeeperCurrencyHelper.formatCurrency(currency)));
                        //ShopkeeperCurrencyHelper.refundPlayer(player, currency);
                    } else {
                        //player.sendSystemMessage(Component.literal("Daily Login Bonus! ").append(ShopkeeperCurrencyHelper.formatCurrency(currency)));
                        //ShopkeeperCurrencyHelper.refundPlayer(player, currency);
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
        Instance.Data.DailyRewardMap.putAll(Config.DailyRewardSettings_Currency);


    }

    public void MarkDirty() {
        WorldDataManager.SaveDataInstance.markDirty();
    }


}
