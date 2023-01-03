package net.petercashel.dingusprimeacm.items.zonetool;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.petercashel.dingusprimeacm.world.zones.ZoneManager;

import java.util.List;

public class ZoneTool extends Item {
    public ZoneTool(Item.Properties properties) {
        super(properties);

    }

    public static Properties GetDefaultItemProperties(Properties properties) {
        //TODO, Setup Properties
        return properties.setNoRepair().stacksTo(1);
    }

    @Override
    public void appendHoverText(ItemStack stack, Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
        tooltip.add(new TextComponent("This tool makes your head explode"));

        super.appendHoverText(stack, worldIn, tooltip, flagIn);
    }


    //To do something when it's right clicked, override use.
    // It takes in the world which lets you effect blocks and stuff, the player that used it and whether it was main or offhand.
    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {
        boolean isCrouch = player.isCrouching();

        //Clear Selection
        if (!world.isClientSide && isCrouch) {

            ZoneManager.Instance.Data.ClearPlayerSelection(player);

        }

        return super.use(world, player, hand);
    }

    @Override
    public InteractionResult useOn(UseOnContext pContext) {
        //Right Click Block
        if (!pContext.getLevel().isClientSide) {
            ZoneManager.Instance.Data.SetPlayerSelectionRight(pContext);
        }

        return InteractionResult.FAIL;
        //return super.useOn(pContext);
    }

    @Override
    public boolean canAttackBlock(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer) {
        //Left Click Block
        if (!pLevel.isClientSide) {
            ZoneManager.Instance.Data.SetPlayerSelectionLeft(pState, pLevel, pPlayer, pPos);
        }

        //Never Break
        return false;

        //return super.canAttackBlock(pState, pLevel, pPos, pPlayer);
    }

    @Override
    public InteractionResult onItemUseFirst(ItemStack stack, UseOnContext context) {

        return super.onItemUseFirst(stack, context);
    }


}
