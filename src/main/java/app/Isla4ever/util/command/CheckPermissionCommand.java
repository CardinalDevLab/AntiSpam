package app.Isla4ever.util.command;

import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;

public class CheckPermissionCommand extends FilterCommand {
    private final String permission;
    private final String MES_NO_PERMISSION;
    public CheckPermissionCommand(AbstractCommand next, String permission, String mes_noPermission) {
        super(next);
        if (permission == null) throw new NullPointerException("permission can not be null.");
        if (mes_noPermission == null) throw new NullPointerException("mes_noPermission can not be null.");
        this.permission = permission;
        MES_NO_PERMISSION = mes_noPermission.replace("%permission%", permission);
    }
    @Override
    protected final boolean check(CommandSender sender, Command cmd, String label, String[] args, int argsIndex) {
        return sender.hasPermission(permission);
    }
    protected void noPermission(CommandSender sender, Command cmd, String label, String[] args, int argsIndex) {
        sender.sendMessage(MES_NO_PERMISSION);
    }
}
