package net.petercashel.dingusprimeacm.commands;

import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraftforge.server.command.EnumArgument;
import net.petercashel.dingusprimeacm.world.Zones.ZoneManager;
import net.petercashel.dingusprimeacm.world.Zones.ZonePermissions;

public class ZoneCommand extends CommandBase {
    public static LiteralArgumentBuilder<CommandSourceStack> BuildCommand(LiteralArgumentBuilder<CommandSourceStack> zone) {
        return zone
                .then(Commands.literal("member")
                        .then(Commands.literal("add")
                                .then(Commands.argument("player", EntityArgument.player())
                                        .executes(commandContext -> ZoneManager.Instance.MemberAdd(commandContext))
                                )
                        )
                        .then(Commands.literal("remove")
                                .then(Commands.argument("player", EntityArgument.player())
                                        .executes(commandContext -> ZoneManager.Instance.MemberRemove(commandContext))
                                )
                        )
                )
                .then(Commands.literal("permissions")
                        .then(Commands.literal("member")
                                .then(Commands.argument("permission", EnumArgument.enumArgument(ZonePermissions.ZonePermissionsEnum.class))
                                        .then(Commands.argument("state", BoolArgumentType.bool())
                                                .executes(commandContext -> ZoneManager.Instance.MemberSetPerm(commandContext))
                                        )
                                )
                        )
                        .then(Commands.literal("public")
                                .then(Commands.argument("permission", EnumArgument.enumArgument(ZonePermissions.ZonePermissionsEnum.class))
                                        .then(Commands.argument("state", BoolArgumentType.bool())
                                                .executes(commandContext -> ZoneManager.Instance.PublicSetPerm(commandContext))
                                        )
                                )
                        )
                )

                .then(Commands.literal("admin")
                        .requires((commandSource) -> commandSource.hasPermission(3))
                        .then(Commands.literal("add")
                                .then(Commands.argument("type", StringArgumentType.word())
                                        .suggests((context, builder) -> {
                                            return ZoneTypes(builder).buildFuture();
                                        })
                                        .then(Commands.argument("startPos", BlockPosArgument.blockPos())
                                                .then(Commands.argument("radius", IntegerArgumentType.integer(1, 64))
                                                        .executes(commandContext -> ZoneManager.Instance.CreateZone(commandContext))
                                                )
                                                .then(Commands.argument("endPos", BlockPosArgument.blockPos())
                                                        .executes(commandContext -> ZoneManager.Instance.CreateZoneEndPos(commandContext))
                                                )
                                                .executes(commandContext -> ZoneManager.Instance.CreateZoneEndPos(commandContext))
                                        )
                                        .then(Commands.argument("playerUUID", EntityArgument.player())
                                                .then(Commands.argument("radius", IntegerArgumentType.integer(1, 64))
                                                        .executes(commandContext -> ZoneManager.Instance.CreateZone(commandContext))
                                                )
                                                .then(Commands.argument("startPos", BlockPosArgument.blockPos())
                                                        .then(Commands.argument("radius", IntegerArgumentType.integer(1, 64))
                                                                .executes(commandContext -> ZoneManager.Instance.CreateZone(commandContext))
                                                        )
                                                        .then(Commands.argument("endPos", BlockPosArgument.blockPos())
                                                                .executes(commandContext -> ZoneManager.Instance.CreateZoneEndPos(commandContext))
                                                        )
                                                        .executes(commandContext -> ZoneManager.Instance.CreateZoneEndPos(commandContext))
                                                )
                                        )
                                )
                        )
                        .then(Commands.literal("remove")
                                .then(Commands.argument("type", StringArgumentType.word())
                                        .suggests((context, builder) -> {
                                            return ZoneTypes(builder).buildFuture();
                                        })
                                        .then(Commands.argument("startPos", BlockPosArgument.blockPos())
                                                .executes(commandContext -> ZoneManager.Instance.RemoveZone(commandContext))
                                        )
                                        .then(Commands.argument("playerUUID", EntityArgument.player())
                                                .then(Commands.argument("startPos", BlockPosArgument.blockPos())
                                                        .executes(commandContext -> ZoneManager.Instance.RemoveZone(commandContext))
                                                )
                                        )
                                        .executes(commandContext -> ZoneManager.Instance.RemoveZone(commandContext))
                                )
                        )
                )
        ;
    }


    private static SuggestionsBuilder ZoneTypes(SuggestionsBuilder builder) {
        return builder.suggest("antibuild").suggest("ownerzone");
    }
}
