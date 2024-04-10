package buildcraft.lib.command;


import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;

//public class CommandBuildCraft extends CommandTreeBase
public class CommandBuildCraft
{
    private static final String NAME = "buildcraft";
    private static final String USAGE = "command.buildcraft.help";

    public static final LiteralArgumentBuilder<CommandSourceStack> COMMAND =
            LiteralArgumentBuilder.<CommandSourceStack>literal(NAME)
                    .requires((req) -> req.hasPermission(0)) // 0->Player 2-> OP
            ;

    //    public CommandBuildCraft()
//    {
//        addSubcommand(new CommandVersion());
//        addSubcommand(new CommandChangelog());
//        addSubcommand(new CommandReloadRegistries());
//    }
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher)
    {
        new CommandVersion().addSubcommand(COMMAND);
        new CommandChangelog().addSubcommand(COMMAND);
        new CommandReloadRegistries().addSubcommand(COMMAND);
        dispatcher.register(COMMAND);
    }

//    @Override
//    public String getName()
//    {
//        return "buildcraft";
//    }

//    @Override
//    public String getUsage(ICommandSender sender)
//    {
//        return "command.buildcraft.help";
//    }
}
