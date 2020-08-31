package app.Isla4ever.util.command;

import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SwitchCommand extends AbstractCommand {
    public final String MES_NO_ARGS;
    public SwitchCommand(String mes_noArgs, String mes_noArgsCase, boolean toLowerCase,
                         Map<String, AbstractCommand> cases) {
        if (mes_noArgs == null) throw new NullPointerException("mes_noArgs can not be null.");
        if (mes_noArgsCase == null) mes_noArgsCase = mes_noArgs;
        if (cases == null) throw new NullPointerException("cases can not be null.");
        MES_NO_ARGS = mes_noArgs;
        MES_NO_ARGS_CASE = mes_noArgsCase;
        TO_LOWER_CASE = toLowerCase;
        CASES = new HashMap<>(cases);
        TABS = new ArrayList<>(CASES.keySet());
    }
    public final String MES_NO_ARGS_CASE;
    public final boolean TO_LOWER_CASE;
    private final HashMap<String, AbstractCommand> CASES;
    private final ArrayList<String> TABS;
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args,
                                      int argsIndex) {
        if (args.length > argsIndex + 1) {
            AbstractCommand next = CASES.get(TO_LOWER_CASE ? args[argsIndex].toLowerCase() : args[argsIndex]);
            if (next != null) return next.onTabComplete(sender, command, label, args, argsIndex + 1);
            return EMPTY_TAB_LIST;
        } else {
            String s = args[argsIndex];
            ArrayList<String> tab = new ArrayList<>(TABS);
            tab.removeIf((x) -> !x.startsWith(s));
            return tab;
        }
    }
    @Override
    public final boolean onCommand(CommandSender sender, Command cmd, String label, String[] args, int argsIndex) {
        if (args.length > argsIndex) {
            AbstractCommand next = CASES.get(TO_LOWER_CASE ? args[argsIndex].toLowerCase() : args[argsIndex]);
            if (next == null) return nextNullCase(sender, cmd, label, args, argsIndex);
            return next.onCommand(sender, cmd, label, args, argsIndex + 1);
        } else {
            return noArgsCase(sender, cmd, label, args, argsIndex);
        }
    }
    protected boolean noArgsCase(CommandSender sender, Command cmd, String label, String[] args, int argsIndex) {
        sender.sendMessage(MES_NO_ARGS);
        return true;
    }
    protected boolean nextNullCase(CommandSender sender, Command cmd, String label, String[] args, int argsIndex) {
        sender.sendMessage(MES_NO_ARGS_CASE.replace("%args%", args[argsIndex]));
        return true;
    }
}
