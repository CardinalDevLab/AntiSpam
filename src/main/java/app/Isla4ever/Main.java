package app.Isla4ever;

import app.Isla4ever.util.command.CommandManager;
import cn.nukkit.event.Listener;
import cn.nukkit.plugin.Plugin;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.utils.Config;
import cn.nukkit.utils.ConfigSection;
import cn.nukkit.utils.TextFormat;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class Main extends PluginBase implements Listener {
    public final static String PluginName = "元路反刷屏插件";
    public static final String WhenTheLanguageFileIsLost = "§c§l[语言文件缺失]§4§l(完全损坏)§c§l节点:%node%";
    private static String LangLost;
    private static String Prefix = "";
    private static Main main;
    public static Main main() {
        return main;
    }
    public static final String t(String s) {
        return TextFormat.colorize('&', s);
    }
    private Config config;
    private void bstats() {

        // 注册bstats
        Metrics metrics = new Metrics(this, 8722);

        metrics.addCustomChart(new Metrics.DrilldownPie("base_config", () -> {
            Map<String, Map<String, Integer>> m = new LinkedHashMap<>();
            ArrayList<String> keys = AntiSpam.getKeys();
            for (String key : keys) {
                LinkedHashMap<String, Integer> mm = new LinkedHashMap<>();
                m.put(key, mm);
                AntiSpam.AntiSpamConfig c = AntiSpam.getConfig(key);
                mm.put("ml", c.minLength);
                mm.put("me", (int) c.minEntropy * 1000);
                mm.put("mae", (int) c.minAverageEntropy * 1000);
                mm.put("sca", c.similarCheckAmount);
                mm.put("sms", (int) c.similarMaxSimilarity * 1000);
                mm.put("smd", c.similarMinDistance);
            }
            return m;
        }));
        metrics.addCustomChart(new Metrics.SimplePie("pls_count", () -> {// 统计元路的插件数量
            int count = 0;
            for (Plugin pl : getServer().getPluginManager().getPlugins().values()) {
                if (pl.getName().startsWith("yuanlu")) count++;
            }
            return Integer.toString(count);
        }));

    }

    public ArrayList<String> list(String node) {
        node = "message." + node;
        if (config.isList(node)) {
            List<String> l = config.getStringList(node);
            ArrayList<String> r = new ArrayList<>(l.size());
            l.forEach((x) -> r.add(x.replace('&', '§')));
            return r;
        } else if (config.isString(node)) {
            String message = config.getString(node);
            List<String> l = Arrays.asList(message.split("\n"));
            ArrayList<String> r = new ArrayList<>(l.size());
            l.forEach((x) -> r.add(x.replace('&', '§')));
            return r;
        } else {
            getLogger().warning("§d[LMES] §c§lcan not find list in config: " + node);
            return new ArrayList<>(Arrays.asList(LangLost.replace("%node%", node)));
        }
    }
    public void reload() {
        unload();
        load();
    }

    /**
     * 加载
     */
    private void load() {
        AntiSpam.load(config.getSection("data"), loadFile("data.yml").getSections());
    }

    /**
     * 卸载
     */
    private void unload() {
        saveFile(AntiSpam.saveData(), "data.yml");
    }
    public void saveFile(Config c, String fileName) {
        try {
            c.save(new File(getDataFolder(), fileName));
        } catch (Exception e) {
        }
    }
    public Config loadFile(String fileName) {
        File file = new File(this.getDataFolder(), fileName);
        if (!file.exists()) try {
            saveResource(fileName, false);
        } catch (IllegalArgumentException e) {
            try {
                file.createNewFile();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        } catch (Exception e1) {
            e1.printStackTrace();
            return null;
        }
        try {
            return new Config(this.getDataFolder()+"/config.yml", Config.YAML);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    public String mes(String node) {
        return mes(node, 0);
    }
    public String mes(String node, int type) {
        node = "message." + node;
        boolean nop = type == -1;
        if (config.isList(node)) {
            List<String> l = config.getStringList(node);
            final StringBuilder sb = new StringBuilder(32);
            l.forEach((x) -> {
                if (!nop) sb.append(Prefix);
                sb.append(x).append('\n');
            });
            if (sb.length() > 0) sb.setLength(sb.length() - 1);
            return t(sb.toString());
        } else if (config.isString(node)) {
            String message = config.getString(node);
            if (nop) return t(message);
            return t(Prefix + message);
        } else {
            getLogger().warning("§d[LMES] §c§lcan not find message in config: " + node);
            if (nop) return LangLost.replace("%node%", node);
            return Prefix + LangLost.replace("%node%", node);
        }
    }
    @Override
    public void onDisable() {
        unload();
        getLogger().info("§a" + PluginName + "-关闭");
    }
    @Override
    public void onEnable() {
        bstats();
        // 启用插件时自动发出
        main = this;
        getLogger().info("§a" + PluginName + "-启动");
        getLogger().info("written by 元路, ported to Nukkit by Isla4ever");
        config = loadFile("config.yml");
        Prefix = config.getString("Prefix", "");
        LangLost = config.getString("message.LanguageFileIsLost", WhenTheLanguageFileIsLost);
        getServer().getPluginManager().registerEvents(AntiSpam.ME, this); // 注册监听器
        CommandManager.init();
        load();
    }


}
