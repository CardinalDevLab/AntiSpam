package app.Isla4ever.util.command;

import app.Isla4ever.AntiSpam;
import app.Isla4ever.AntiSpamTool;
import app.Isla4ever.Main;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public final class CommandManager {
    public static final String VERSION = "1.0.0";
    public static final long VERSION_NUMBER = 1;
    public static String getVersion() {
        return VERSION;
    }
    public static long getVersionNumber() {
        return VERSION_NUMBER;
    }
    public static final void init() {
        final String name = "yantispam";
        final String description = "元路反刷屏插件命令";
        final String usageMessage = "/yantispam help";
        final List<String> aliases = Arrays.asList();
        HashMap<String, AbstractCommand> m = new HashMap<>();
        m.put("reload", new AbstractCommand() {

            String reload = mes("cmd.reload.successful");

            @Override
            public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args, int argsIndex) {
                Main.main().reload();
                sender.sendMessage(reload);
                return true;
            }

            @Override
            public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args,
                                              int argsIndex) {
                return EMPTY_TAB_LIST;
            }
        });
        m.put("test", new AbstractCommand() {
            @Override
            public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args,
                                              int argsIndex) {
                return EMPTY_TAB_LIST;
            }

            String simple = mes("cmd.test.simple");

            String muti = mes("cmd.test.muti");

            String help = mes("cmd.test.help");
            @Override
            public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args, int argsIndex) {
                if (args.length > argsIndex) {
                    String s = "";
                    for (int i = argsIndex; i < args.length; i++)
                        s += args[i];
                    if (s.contains("#")) {
                        String[] ss = s.split("#");
                        for (int i = 0; i < ss.length; i++)
                            sender.sendMessage(simple.replace("%s%", ss[i])//
                                    .replace("%e%", String.valueOf(AntiSpamTool.entropy(ss[i])))//
                                    .replace("%ae%", String.valueOf(AntiSpamTool.averageEntropy(ss[i])))//
                            );
                        for (int i = 0; i < ss.length; i++)
                            for (int j = i + 1; j < ss.length; j++) {
                                sender.sendMessage(muti.replace("%s1%", ss[i])//
                                        .replace("%s2%", ss[j])//
                                        .replace("%s%", String.valueOf(AntiSpamTool.getSimilarity(ss[i], ss[j])))//
                                        .replace("%d%", String.valueOf(AntiSpamTool.getMinDistance(ss[i], ss[j])))//
                                );
                            }
                    } else {
                        sender.sendMessage(simple.replace("%s%", s)//
                                .replace("%e%", String.valueOf(AntiSpamTool.entropy(s)))//
                                .replace("%ae%", String.valueOf(AntiSpamTool.averageEntropy(s)))//
                        );
                        return true;
                    }
                } else {
                    sender.sendMessage(help);
                    return true;
                }
                return true;
            }
        });
        m.put("set", new AbstractCommand() {

            List<String> tab_st = list("tab.set.st");

            List<String> tab_smd = list("tab.set.smd");

            List<String> tab_sms = list("tab.set.sms");

            List<String> tab_sca = list("tab.set.sca");

            List<String> tab_me = list("tab.set.me");

            List<String> tab_mae = list("tab.set.mae");

            List<String> tab_ml = list("tab.set.ml");

            List<String> tab_2 = list("tab.set.two-type");
            @Override
            public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args,
                                              int argsIndex) {
                if (args.length > argsIndex + 1) {
                    String __ = args[argsIndex + 1];
                    if (__.isEmpty())
                        return tab_2;
                    else
                        try {
                            Integer.parseInt(__);
                            if (args.length > argsIndex + 7)
                                return tab_st;
                            else if (args.length > argsIndex + 6)
                                return tab_smd;
                            else if (args.length > argsIndex + 5)
                                return tab_sms;
                            else if (args.length > argsIndex + 4)
                                return tab_sca;
                            else if (args.length > argsIndex + 3)
                                return tab_me;
                            else if (args.length > argsIndex + 2)
                                return tab_mae;
                            else
                                return tab_ml;
                        } catch (Exception e) {
                            if (args.length > 2)
                                return EMPTY_TAB_LIST;
                            else
                                return getConfigKeys();
                        }
                } else if (args.length > argsIndex)
                    return null;
                else
                    return EMPTY_TAB_LIST;
            }

            String help = mes("cmd.set.help");

            String not_num = mes("cmd.set.not-number");

            String success = mes("cmd.set.successful");

            @Override
            public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args, int argsIndex) {
                if (args.length > argsIndex) {
                    String player_str = args[argsIndex];
                    if (args.length > argsIndex + 1) {
                        if (args.length == argsIndex + 2) {
                            String key = AntiSpam.setPlayerConfig(player_str, AntiSpam.getConfig(args[argsIndex + 1]));
                            sender.sendMessage(success.replace("%key%", key));
                            return true;
                        } else if (args.length > argsIndex + 6) {
                            try {
                                String ml_ = args[argsIndex + 1];
                                String mae_ = args[argsIndex + 2];
                                String me_ = args[argsIndex + 3];
                                String sca_ = args[argsIndex + 4];
                                String sms_ = args[argsIndex + 5];
                                String smd_ = args[argsIndex + 6];
                                String st_ = args[argsIndex + 6];
                                int ml = Integer.parseInt(ml_);
                                double mae = Double.parseDouble(mae_);
                                double me = Double.parseDouble(me_);
                                int sca = Integer.parseInt(sca_);
                                double sms = Double.parseDouble(sms_);
                                int smd = Integer.parseInt(smd_);
                                long st = Long.parseLong(st_);
                                AntiSpam.AntiSpamConfig c = new AntiSpam.AntiSpamConfig(ml, mae, me, sca, sms, smd, st);
                                String key = AntiSpam.setPlayerConfig(player_str, c);
                                sender.sendMessage(success.replace("%key%", key));
                                return true;
                            } catch (Exception e) {
                                sender.sendMessage(not_num);
                                return true;
                            }
                        } else {
                            sender.sendMessage(help);
                            return true;
                        }
                    } else {
                        String key = AntiSpam.setPlayerConfig(player_str, AntiSpam.BASE_CONFIG);
                        sender.sendMessage(success.replace("%key%", key));
                        return true;
                    }
                } else {
                    sender.sendMessage(help);
                    return true;
                }
            }
        });
        AbstractCommand COMMAND = new CheckPermissionCommand(
                new SwitchCommand(Main.main().mes("cmd.help"), null, true, m), "yuanlu.antiSpam.admin",
                mes("no-permission"));

        RootCommand root = new RootCommand(name, description, usageMessage, aliases, COMMAND);
        root.register();
    }
    private CommandManager() {
    }
    protected static String mes(String node) {
        return mes(node, 0);
    }
    protected static String mes(String node, int type) {
        return Main.main().mes(node, type);
    }
    private static int CONFIG_modCount;
    private static ArrayList<String> CONFIG_KEY;
    static ArrayList<String> getConfigKeys() {
        if (CONFIG_KEY == null || CONFIG_modCount != AntiSpam.getCONFIGModCount()) {
            CONFIG_modCount = AntiSpam.getCONFIGModCount();
            CONFIG_KEY = AntiSpam.getKeys();
        }
        return CONFIG_KEY;
    }

}
