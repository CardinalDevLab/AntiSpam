package app.Isla4ever.util.command;

import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;

import javax.annotation.Nonnull;
import java.util.List;

public abstract class FilterCommand extends AbstractCommand {
    protected final AbstractCommand next;
    public FilterCommand(@Nonnull AbstractCommand next) {
        if (next == null) throw new NullPointerException("next can not be null.");
        this.next = next;
    }
    @Override
    public final boolean onCommand(CommandSender sender, Command cmd, String label, String[] args, int argsIndex) {
        if (check(sender, cmd, label, args, argsIndex)) return next.onCommand(sender, cmd, label, args, argsIndex);
        return true;
    }
    @Override
    public final List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args,
                                            int argsIndex) {
        return next.onTabComplete(sender, command, label, args, argsIndex);
    }
    protected abstract boolean check(CommandSender sender, Command cmd, String label, String[] args, int argsIndex);
}
