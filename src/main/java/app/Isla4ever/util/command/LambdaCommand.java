package app.Isla4ever.util.command;

import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;

import java.util.List;

public class LambdaCommand extends AbstractCommand {
    @FunctionalInterface
    public static interface OnCommand {
        boolean onCommand(CommandSender sender, Command cmd, String label, String[] args, int argsIndex);
    }
    @FunctionalInterface
    public static interface OnTabComplete {
        List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args, int argsIndex);
    }
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args, int argsIndex) {
        return command.onCommand(sender, cmd, label, args, argsIndex);
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args,
                                      int argsIndex) {
        return tab.onTabComplete(sender, command, label, args, argsIndex);
    }
    private final OnCommand command;
    private final OnTabComplete tab;
    public LambdaCommand(OnCommand command, OnTabComplete tab) {
        if (command == null) throw new NullPointerException("command can not be null.");
        if (tab == null) throw new NullPointerException("tab can not be null.");
        this.command = command;
        this.tab = tab;
    }


}
