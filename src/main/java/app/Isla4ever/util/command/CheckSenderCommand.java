package app.Isla4ever.util.command;

import cn.nukkit.Player;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;

import java.util.HashMap;

public class CheckSenderCommand extends FilterCommand {
    public static enum CheckType {
        EXTENDS,
        EQUAL,
        NOTEXTENDS,
        NOTEQUAL;
    }
    public static final HashMap<Class<? extends CommandSender>, String> TYPE_NAME;
    public static final HashMap<Class<? extends CommandSender>, String> SIMPLE_NAME;
    public static final boolean USE_NAME_CACHE = true;
    static {
        if (USE_NAME_CACHE) {
            TYPE_NAME = new HashMap<>();
            SIMPLE_NAME = new HashMap<>();
        } else {
            TYPE_NAME = null;
            SIMPLE_NAME = null;
        }
    }
    public final Class<? extends CommandSender> clazz;
    public final CheckType type;
    public final String MES_NOT_PASS;
    public CheckSenderCommand(AbstractCommand next, Class<? extends CommandSender> clazz, CheckType type,
                              String mes_notPass) {
        super(next);
        if (clazz == null) throw new NullPointerException("clazz can not be null.");
        if (type == null) throw new NullPointerException("type can not be null.");
        if (mes_notPass == null) throw new NullPointerException("mes_notPass can not be null.");
        this.clazz = clazz;
        this.type = type;
        this.MES_NOT_PASS = mes_notPass;
    }
    public CheckSenderCommand(AbstractCommand next, String mes_notPass) {
        this(next, Player.class, CheckType.EXTENDS, mes_notPass);
    }
    @Override
    public final boolean check(CommandSender sender, Command cmd, String label, String[] args, int argsIndex) {
        boolean ok = false;
        Class<? extends CommandSender> c = sender.getClass();
        switch (type) {
            case EQUAL:
                ok = c == clazz;
                break;
            case EXTENDS:
                ok = clazz.isAssignableFrom(c);
                break;
            case NOTEQUAL:
                ok = c != clazz;
                break;
            case NOTEXTENDS:
                ok = !clazz.isAssignableFrom(c);
                break;
        }
        if (!ok) return noArgsCase(sender, cmd, label, args, argsIndex, c);
        return true;
    }
    protected final String getCSname(Class<? extends CommandSender> c) {
        if (!USE_NAME_CACHE) return c.getSimpleName();
        String name = SIMPLE_NAME.get(c);
        if (name == null) {
            name = c.getSimpleName();
            SIMPLE_NAME.put(c, name);
        }
        return name;
    }
    protected final String getCTname(Class<? extends CommandSender> c) {
        if (!USE_NAME_CACHE) return c.getTypeName();
        String name = TYPE_NAME.get(c);
        if (name == null) {
            name = c.getTypeName();
            TYPE_NAME.put(c, name);
        }
        return name;
    }
    protected boolean noArgsCase(CommandSender sender, Command cmd, String label, String[] args, int argsIndex,
                                 Class<? extends CommandSender> senderClass) {
        sender.sendMessage(
                MES_NOT_PASS.replace("%sender%", getCSname(senderClass)).replace("%class%", getCTname(senderClass)));
        return true;
    }
}
