package app.Isla4ever.util.command;

import app.Isla4ever.Main;
import cn.nukkit.Server;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandMap;
import cn.nukkit.command.CommandSender;

import javax.annotation.Nonnull;
import java.lang.reflect.Method;
import java.util.List;

public class RootCommand extends Command {
    private final AbstractCommand handle;
    public RootCommand(@Nonnull String name, @Nonnull String description, @Nonnull String usageMessage,
                       @Nonnull List<String> aliases, @Nonnull AbstractCommand handle) {
        super(name, description, usageMessage, aliases.toArray(new String[aliases.size()]));
        if (handle == null) throw new NullPointerException("handle can not be null.");
        this.handle = handle;
    }
    @Override
    public boolean execute(CommandSender sender, String alias, String[] args) {
        return handle.onCommand(sender, this, alias, args, 0);
    }
    //@Override
    public List<String> tabComplete(CommandSender sender, String alias, String[] args) throws IllegalArgumentException {
        return handle.onTabComplete(sender, this, alias, args, 0);
    }
    public void register() {
        try {
            Server.getInstance().getCommandMap().register(Main.main().getName(), this);
        } catch (Throwable e) {
            try {
                Method method = Server.getInstance().getClass().getMethod("getCommandMap");
                CommandMap cmdm = (CommandMap) method.invoke(Server.getInstance());
                cmdm.register(Main.main().getName(), this);
            } catch (Exception e2) {
                System.err.println("CAN NOT REGISTER COMMAND: " + e2.toString());
                Main.main().getLogger().warning("CAN NOT REGISTER COMMAND: " + e2.toString());
            }
        }
    }


}
