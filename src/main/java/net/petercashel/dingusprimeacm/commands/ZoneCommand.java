package net.petercashel.dingusprimeacm.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.server.command.EnumArgument;
import net.petercashel.dingusprimeacm.configuration.DPAcmConfig;
import net.petercashel.dingusprimeacm.world.zones.ZoneManager;
import net.petercashel.dingusprimeacm.world.zones.ZonePermissions;
import net.petercashel.dingusprimeacm.world.zones.selection.PlayerSelectionSession;

public class ZoneCommand extends CommandBase {
    public static LiteralArgumentBuilder<CommandSourceStack> BuildCommand(LiteralArgumentBuilder<CommandSourceStack> zone) {
        return zone
                .requires((commandSourceStack -> {
                    try {
                        return commandSourceStack.getPlayerOrException().level.dimension() == Level.OVERWORLD;
                    } catch (CommandSyntaxException e) {
                        commandSourceStack.sendFailure(new TextComponent("Zones only operate in the Overworld."));
                        return false;
                    }
                }))
                .then(Commands.literal("member")
                        .then(Commands.literal("add")
                                .then(Commands.argument("player", EntityArgument.player())
                                        .executes(commandContext -> ZoneManager.Instance.MemberAdd(commandContext, false))
                                )
                        )
                        .then(Commands.literal("remove")
                                .then(Commands.argument("player", EntityArgument.player())
                                        .executes(commandContext -> ZoneManager.Instance.MemberRemove(commandContext, false))
                                )
                        )
                )
                .then(Commands.literal("name")
                        .then(Commands.argument("newName", StringArgumentType.greedyString())
                                .executes(commandContext -> ZoneManager.Instance.SetName(commandContext, false))
                        )
                )
                .then(Commands.literal("permissions")
                        .then(Commands.literal("member")
                                .then(Commands.argument("permission", EnumArgument.enumArgument(ZonePermissions.ZonePermissionsEnum.class))
                                        .then(Commands.argument("state", BoolArgumentType.bool())
                                                .executes(commandContext -> ZoneManager.Instance.MemberSetPerm(commandContext, false))
                                        )
                                )
                        )
                        .then(Commands.literal("public")
                                .then(Commands.argument("permission", EnumArgument.enumArgument(ZonePermissions.ZonePermissionsEnum.class))
                                        .then(Commands.argument("state", BoolArgumentType.bool())
                                                .executes(commandContext -> ZoneManager.Instance.PublicSetPerm(commandContext, false))
                                        )
                                )
                        )
                )


        ;
    }

    public static LiteralArgumentBuilder<CommandSourceStack> BuildPlotCommand(LiteralArgumentBuilder<CommandSourceStack> plot) {
        return plot
                .requires((commandSourceStack -> {
                    try {
                        return commandSourceStack.getPlayerOrException().level.dimension() == Level.OVERWORLD;
                    } catch (CommandSyntaxException e) {
                        commandSourceStack.sendFailure(new TextComponent("Zones only operate in the Overworld."));
                        return false;
                    }
                }))
                .then(Commands.literal("create")
                        .executes(commandContext -> ZoneManager.Instance.CreateSubzone(commandContext))
                )
                .then(Commands.literal("delete")
                        .executes(commandContext -> ZoneManager.Instance.RemoveSubzone(commandContext))
                )

                .then(Commands.literal("setOwner")
                        .then(Commands.argument("player", EntityArgument.player())
                                .executes(commandContext -> ZoneManager.Instance.SetOwnerSubzone(commandContext))
                        )
                )

                .then(Commands.literal("member")
                        .then(Commands.literal("add")
                                .then(Commands.argument("player", EntityArgument.player())
                                        .executes(commandContext -> ZoneManager.Instance.MemberAdd(commandContext, true))
                                )
                        )
                        .then(Commands.literal("remove")
                                .then(Commands.argument("player", EntityArgument.player())
                                        .executes(commandContext -> ZoneManager.Instance.MemberRemove(commandContext, true))
                                )
                        )
                )


                .then(Commands.literal("name")
                        .then(Commands.argument("newName", StringArgumentType.greedyString())
                                .executes(commandContext -> ZoneManager.Instance.SetName(commandContext, true))
                        )
                )

                .then(Commands.literal("permissions")
                        .then(Commands.literal("member")
                                .then(Commands.argument("permission", EnumArgument.enumArgument(ZonePermissions.ZonePermissionsEnum.class))
                                        .then(Commands.argument("state", BoolArgumentType.bool())
                                                .executes(commandContext -> ZoneManager.Instance.MemberSetPerm(commandContext, true))
                                        )
                                )
                        )
                        .then(Commands.literal("public")
                                .then(Commands.argument("permission", EnumArgument.enumArgument(ZonePermissions.ZonePermissionsEnum.class))
                                        .then(Commands.argument("state", BoolArgumentType.bool())
                                                .executes(commandContext -> ZoneManager.Instance.PublicSetPerm(commandContext, true))
                                        )
                                )
                        )
                )

        ;
    }

    public static LiteralArgumentBuilder<CommandSourceStack> BuildAdminCommand(LiteralArgumentBuilder<CommandSourceStack> zoneadmin) {
        return zoneadmin
                .requires((commandSourceStack -> {
                    try {
                        return commandSourceStack.getPlayerOrException().level.dimension() == Level.OVERWORLD;
                    } catch (CommandSyntaxException e) {
                        commandSourceStack.sendFailure(new TextComponent("Zones only operate in the Overworld."));
                        return false;
                    }
                }))
                .requires((commandSource) -> commandSource.hasPermission(3))
                .then(Commands.literal("add")
                        .then(Commands.argument("type", EnumArgument.enumArgument(ZoneManager.ZoneTypeEnum.class))
                                .then(Commands.argument("radius", IntegerArgumentType.integer(1, DPAcmConfig.ConfigInstance.ZoneSettings.MaxZoneCreate_Radius))
                                        .executes(commandContext -> ZoneManager.Instance.CreateZone(commandContext))
                                )
                                .then(Commands.argument("playerUUID", EntityArgument.player())
                                        .then(Commands.argument("radius", IntegerArgumentType.integer(1, DPAcmConfig.ConfigInstance.ZoneSettings.MaxZoneCreate_Radius))
                                                .executes(commandContext -> ZoneManager.Instance.CreateZone(commandContext))
                                        )
                                        .executes(commandContext -> ZoneManager.Instance.CreateZoneTool(commandContext))
                                )
                                .executes(commandContext -> ZoneManager.Instance.CreateZoneTool(commandContext))
                        )
                )
                .then(Commands.literal("remove")
                        .then(Commands.argument("type", EnumArgument.enumArgument(ZoneManager.ZoneTypeEnum.class))
                                .then(Commands.argument("playerUUID", EntityArgument.player())
                                        .executes(commandContext -> ZoneManager.Instance.RemoveZone(commandContext))
                                )
                                .executes(commandContext -> ZoneManager.Instance.RemoveZone(commandContext))
                        )
                )
                ;
    }

    public static LiteralArgumentBuilder<CommandSourceStack> BuildWandCommand(LiteralArgumentBuilder<CommandSourceStack> zonewand) {
        return zonewand
                .requires((commandSourceStack -> {
                    try {
                        return commandSourceStack.getPlayerOrException().level.dimension() == Level.OVERWORLD;
                    } catch (CommandSyntaxException e) {
                        commandSourceStack.sendFailure(new TextComponent("Zones only operate in the Overworld."));
                        return false;
                    }
                }))
                .then(Commands.literal("expand")
                        .then(Commands.argument("blocksForward", IntegerArgumentType.integer())
                                .then(Commands.argument("facing", EnumArgument.enumArgument(Direction.class))
                                        .executes(commandContext -> {
                                            Player player = commandContext.getSource().getPlayerOrException();
                                            Direction facing = commandContext.getArgument("facing", Direction.class);
                                            int count = IntegerArgumentType.getInteger(commandContext, "blocksForward");
                                            //int countBack = IntegerArgumentType.getInteger(commandContext, "blocksBackward");
                                            PlayerSelectionSession session = ZoneManager.Instance.Data.GetPlayerSelection(commandContext.getSource().getPlayerOrException());

                                            session.expandSelection(facing, count, 0);

                                            ZoneManager.Instance.Data.SendSelectionToClient(player);
                                            return Command.SINGLE_SUCCESS;
                                        })
                                )
                                .then(Commands.argument("blocksBackward", IntegerArgumentType.integer())
                                        .then(Commands.argument("facing", EnumArgument.enumArgument(Direction.class))
                                                .executes(commandContext -> {
                                                    Player player = commandContext.getSource().getPlayerOrException();
                                                    Direction facing = commandContext.getArgument("facing", Direction.class);
                                                    int count = IntegerArgumentType.getInteger(commandContext, "blocksForward");
                                                    int countBack = IntegerArgumentType.getInteger(commandContext, "blocksBackward");
                                                    PlayerSelectionSession session = ZoneManager.Instance.Data.GetPlayerSelection(commandContext.getSource().getPlayerOrException());

                                                    session.expandSelection(facing, count, countBack);

                                                    ZoneManager.Instance.Data.SendSelectionToClient(player);
                                                    return Command.SINGLE_SUCCESS;
                                                })
                                        )
                                )
                        )
                )
                .then(Commands.literal("contract")
                        .then(Commands.argument("blocksForward", IntegerArgumentType.integer())
                                .then(Commands.argument("facing", EnumArgument.enumArgument(Direction.class))
                                        .executes(commandContext -> {
                                            Player player = commandContext.getSource().getPlayerOrException();
                                            Direction facing = commandContext.getArgument("facing", Direction.class);
                                            int count = IntegerArgumentType.getInteger(commandContext, "blocksForward");
                                            //int countBack = IntegerArgumentType.getInteger(commandContext, "blocksBackward");
                                            PlayerSelectionSession session = ZoneManager.Instance.Data.GetPlayerSelection(commandContext.getSource().getPlayerOrException());

                                            session.contractSelection(facing, count, 0);

                                            ZoneManager.Instance.Data.SendSelectionToClient(player);
                                            return Command.SINGLE_SUCCESS;
                                        })
                                )
                                .then(Commands.argument("blocksBackward", IntegerArgumentType.integer())
                                        .then(Commands.argument("facing", EnumArgument.enumArgument(Direction.class))
                                                .executes(commandContext -> {
                                                    Player player = commandContext.getSource().getPlayerOrException();
                                                    Direction facing = commandContext.getArgument("facing", Direction.class);
                                                    int count = IntegerArgumentType.getInteger(commandContext, "blocksForward");
                                                    int countBack = IntegerArgumentType.getInteger(commandContext, "blocksBackward");
                                                    PlayerSelectionSession session = ZoneManager.Instance.Data.GetPlayerSelection(commandContext.getSource().getPlayerOrException());

                                                    session.contractSelection(facing, count, countBack);

                                                    ZoneManager.Instance.Data.SendSelectionToClient(player);
                                                    return Command.SINGLE_SUCCESS;
                                                })
                                        )
                                )
                        )
                )
                .then(Commands.literal("shift")
                        .then(Commands.argument("blocksForward", IntegerArgumentType.integer())
                                .then(Commands.argument("facing", EnumArgument.enumArgument(Direction.class))
                                        .executes(commandContext -> {
                                            Player player = commandContext.getSource().getPlayerOrException();
                                            Direction facing = commandContext.getArgument("facing", Direction.class);
                                            int count = IntegerArgumentType.getInteger(commandContext, "blocksForward");
                                            //int countBack = IntegerArgumentType.getInteger(commandContext, "blocksBackward");
                                            PlayerSelectionSession session = ZoneManager.Instance.Data.GetPlayerSelection(commandContext.getSource().getPlayerOrException());

                                            session.shiftSelection(facing, count);

                                            ZoneManager.Instance.Data.SendSelectionToClient(player);
                                            return Command.SINGLE_SUCCESS;
                                        })
                                )
                        )
                )
                ;
    }
}
