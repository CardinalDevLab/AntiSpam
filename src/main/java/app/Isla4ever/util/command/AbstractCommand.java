package app.Isla4ever.util.command;

import app.Isla4ever.Main;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class AbstractCommand {
    protected static final List<String> EMPTY_TAB_LIST = Arrays.asList();
    protected static ArrayList<String> list(String node) {
        return Main.main().list(node);
    }
    protected static String mes(String node) {
        return mes(node, 0);
    }
    protected static String mes(String node, int type) {
        return Main.main().mes(node, type);
    }
    public abstract boolean onCommand(CommandSender sender, Command cmd, String label, String[] args, int argsIndex);
    public abstract List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args,
                                               int argsIndex);

}
