package net.petercashel.dingusprimeacm.shopkeeper.container;

import com.tm.calemicore.util.helper.StringHelper;
import com.tm.calemieconomy.item.ItemWallet;
import com.tm.calemieconomy.util.IItemCurrencyHolder;
import com.tm.calemieconomy.util.helper.CurrencyHelper;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class ShopkeeperCurrencyHelper {

    public static boolean hasWallet(Player player) {
        ItemStack stack = CurrencyHelper.getCurrentWallet(player);
        return stack != null && !stack.isEmpty();
    }
    public static ItemStack getCurrentWallet(Player player) {
        //ItemWallet
        ItemStack stack = CurrencyHelper.getCurrentWallet(player);
        if (!(stack.getItem() instanceof ItemWallet)) return null;

        return stack;
    }

    private static IItemCurrencyHolder getWalletFromStack(ItemStack stack) {
        ItemWallet wallet = (ItemWallet) stack.getItem();
        return wallet;
    }

    public static int getBalance(Player player)
    {
        if (!hasWallet(player)) return 0;

        ItemStack stack = getCurrentWallet(player);
        if (stack == null || stack.isEmpty()) return 0;

        IItemCurrencyHolder wallet = getWalletFromStack(stack);

        return wallet.getCurrency(stack);
    }

    public static MutableComponent getBalanceTextComponent(Player player)
    {
        return formatCurrency(getBalance(player));
    }

    public static MutableComponent formatCurrency(int amount) {
        return new TextComponent(StringHelper.insertCommas(amount)).append(new TranslatableComponent("ce.rc"));
    }


    public static boolean canAfford(Player player, float amount)
    {
        if (!hasWallet(player)) return false;

        ItemStack stack = getCurrentWallet(player);
        if (stack == null || stack.isEmpty()) return false;

        IItemCurrencyHolder wallet = getWalletFromStack(stack);

        return wallet.canWithdrawCurrency(stack, (int) amount);
    }

    public static boolean invoicePlayer(Player player, float amount)
    {
        if (player.level.isClientSide) {
            return canAfford(player,amount); //Do not charge player client side.
        }

        if (!hasWallet(player)) return false;

        ItemStack stack = getCurrentWallet(player);
        if (stack == null || stack.isEmpty()) return false;

        IItemCurrencyHolder wallet = getWalletFromStack(stack);
        wallet.withdrawCurrency(stack, (int) amount);

        return true;
    }

    public static boolean canRefundPlayer(Player player, float amount)
    {
        if (!hasWallet(player)) return false;

        ItemStack stack = getCurrentWallet(player);
        if (stack == null || stack.isEmpty()) return false;

        IItemCurrencyHolder wallet = getWalletFromStack(stack);

        return wallet.canDepositCurrency(stack, (int) amount);
    }

    public static boolean refundPlayer(Player player, float amount)
    {
        if (!hasWallet(player)) return false;

        ItemStack stack = getCurrentWallet(player);
        if (stack == null || stack.isEmpty()) return false;

        IItemCurrencyHolder wallet = getWalletFromStack(stack);
        wallet.depositCurrency(stack, (int) amount);

        return true;
    }


}
