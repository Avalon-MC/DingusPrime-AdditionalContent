package net.petercashel.dingusprimeacm;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;

// An example config class. This is not required, but it's a good idea to have one to keep your config organized.
// Demonstrates how to use Neo's config APIs
@EventBusSubscriber(modid = DingusPrimeAdditionalContentMod.MODID, bus = EventBusSubscriber.Bus.MOD)
public class Config
{
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();


    public static final ModConfigSpec.ConfigValue<String> MAGIC_NUMBER_INTRODUCTION = BUILDER
            .comment("What you want the introduction message to be for the magic number")
            .define("magicNumberIntroduction", "The magic number is... ");



    private static final ModConfigSpec.BooleanValue FILENAME_HASHTAG = BUILDER
            .comment("fileNameHashTag")
            .define("data.export.fileNameHashTag", false);


    public enum ShopSortEnum {
        Name,
        NamePrice,
        Price,
        PriceName,
    }

    private static final ModConfigSpec.EnumValue<ShopSortEnum> SHOP_SORT_TYPE = BUILDER
            .comment("shopSortType")
            .defineEnum("shop.settings.shopSortType", ShopSortEnum.PriceName);


    private static final ModConfigSpec.IntValue RANDOM_SHOP_TRADES_COUNT = BUILDER
            .comment("randomShopTradesCount")
            .defineInRange("shop.settings.randomShopTradesCount", 3, 1, 10);


    private static final ModConfigSpec.BooleanValue DAILY_REWARDS_ENABLED = BUILDER
            .comment("DailyRewardsEnabled")
            .define("daily_rewards.settings.enabled", false);


    private static final ModConfigSpec.BooleanValue DAILY_CURRENCY_REWARDS_ENABLED = BUILDER
            .comment("DailyRewardsEnabled")
            .define("daily_rewards.currency.enabled", false);

    // a list of strings that are treated as resource locations for items
    private static final ModConfigSpec.ConfigValue<List<? extends String>> DAILY_CURRENCY_REWARDS_ARRAY = BUILDER
            .comment("A list of numeric pairs for daily currency rewards. Format is 'day,amount'. where day starts at 0 for first login and amount is the amount of currency to give.")
            .defineListAllowEmpty("daily_rewards.currency.day_value_pairs", List.of("0,50", "1,5","3,10","7,30","14,50"), () -> "21,50", Config::validateDailyCurrencyRewardsArrayFormat);

    static final ModConfigSpec SPEC = BUILDER.build();

    public static boolean logDirtBlock = false;;

    private static boolean validateDailyCurrencyRewardsArrayFormat(final Object obj)
    {
        return obj instanceof String itemName &&
                itemName.contains(",") &&
                itemName.split(",").length == 2 &&
                itemName.split(",")[0].chars().allMatch(Character::isDigit) &&
                itemName.split(",")[1].chars().allMatch(Character::isDigit);
    }

    private static boolean validateItemName(final Object obj)
    {
        return obj instanceof String itemName && BuiltInRegistries.ITEM.containsKey(ResourceLocation.parse(itemName));
    }


    public static boolean DataExportSettings_FileNameHashTag;

    public static Config.ShopSortEnum ShopSettings_SortType = Config.ShopSortEnum.PriceName;
    public static int ShopSettings_RandomShopTradesCount;

    public static boolean DailyRewardSettings_Enabled;
    public static boolean DailyRewardSettings_Currency_Enabled;
    public static ConcurrentHashMap<Integer, Integer> DailyRewardSettings_Currency;

    @SubscribeEvent
    static void onLoad(final ModConfigEvent event)
    {
        //logDirtBlock = false;

        DataExportSettings_FileNameHashTag = FILENAME_HASHTAG.get();


        ShopSettings_SortType = SHOP_SORT_TYPE.get();
        ShopSettings_RandomShopTradesCount = RANDOM_SHOP_TRADES_COUNT.get();
        DailyRewardSettings_Enabled = DAILY_REWARDS_ENABLED.get();
        DailyRewardSettings_Currency_Enabled = DAILY_CURRENCY_REWARDS_ENABLED.get();

        DailyRewardSettings_Currency = new ConcurrentHashMap<Integer, Integer>();

        List<? extends String> dailyCurrencyRewardsArray = DAILY_CURRENCY_REWARDS_ARRAY.get();

        for (String s : dailyCurrencyRewardsArray) {
            if (validateDailyCurrencyRewardsArrayFormat(s)) {
                String[] parts = s.split(",");
                DailyRewardSettings_Currency.put(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]));
            }
        }




    }
}
