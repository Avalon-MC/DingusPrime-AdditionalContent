package net.petercashel.dingusprimeacm.shopkeeper.registry;

import net.petercashel.dingusprimeacm.configuration.DPAcmConfig;

public class ShopTradeInfoComparater implements java.util.Comparator<ShopTradeInfo> {

    public static ShopTradeInfoComparater instance = new ShopTradeInfoComparater();


    @Override
    public int compare(ShopTradeInfo o1, ShopTradeInfo o2) {

        DPAcmConfig.ShopSortEnum sort = DPAcmConfig.ConfigInstance.ShopSettings.SortType;

        switch (sort) {
            case Name -> {
                int res = o1.TryGetName().toString().compareTo(o2.TryGetName().toString());
                return res;
            }
            case NamePrice -> {
                int res = o1.TryGetName().toString().compareTo(o2.TryGetName().toString());
                if (res != 0) return res;

                return Integer.compare(o1.Cost, o2.Cost);
            }
            case Price -> {
                int res = Integer.compare(o1.Cost, o2.Cost);
                return res;
            }
            case PriceName -> {
                int res = Integer.compare(o1.Cost, o2.Cost);
                if (res != 0) return res;

                return o1.TryGetName().toString().compareTo(o2.TryGetName().toString());
            }
        }

        int res = Integer.compare(o1.Cost, o2.Cost);
        if (res != 0) return res;

        return o1.TryGetName().toString().compareTo(o2.TryGetName().toString());
    }

}
