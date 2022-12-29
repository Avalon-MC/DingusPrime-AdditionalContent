package net.petercashel.dingusprimeacm.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.Util;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.UuidArgument;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.entity.player.Player;
import net.petercashel.dingusprimeacm.configuration.DPAcmConfig;
import net.petercashel.dingusprimeacm.shopkeeper.registry.ShopTradeManager;
import net.petercashel.dingusprimeacm.world.Zones.ZoneManager;

public class DingusPrimeAcmCommand extends CommandBase{

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher){



        LiteralArgumentBuilder<CommandSourceStack> commandBuilder = Commands.literal("dingusprimeacm")
                .requires((commandSource) -> commandSource.hasPermission(3))
                .then(Commands.literal("resettrades")
                        .executes(commandContext -> executeResetTraders(commandContext))
                )
                .then(Commands.literal("reloadconfig")
                        .executes(commandContext -> executeReloadConfig(commandContext))
                )




                //Fallback
                ;

        dispatcher.register(commandBuilder);

        dispatcher.register(ZoneCommand.BuildCommand(Commands.literal("zone")));
    }


    private static int fail(CommandContext<CommandSourceStack> command){
        return 0;
    }

    private static int executeReloadConfig(CommandContext<CommandSourceStack> command){
        DPAcmConfig.LoadConfig();
        if(command.getSource().getEntity() instanceof Player){
            Player player = (Player) command.getSource().getEntity();
            player.sendMessage(new TextComponent("Reloaded Config"), Util.NIL_UUID);
        }
        return Command.SINGLE_SUCCESS;
    }

    private static int executeResetTraders(CommandContext<CommandSourceStack> command){
        ShopTradeManager.INSTANCE.ResetAll();
        if(command.getSource().getEntity() instanceof Player){
            Player player = (Player) command.getSource().getEntity();
            player.sendMessage(new TextComponent("Trades Reset"), Util.NIL_UUID);
        }
        return Command.SINGLE_SUCCESS;
    }
}
