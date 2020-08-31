package app.Isla4ever;

import cn.nukkit.Player;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.EventPriority;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.PlayerChatEvent;
import cn.nukkit.utils.Config;
import cn.nukkit.utils.ConfigSection;

import java.util.*;

public final class AntiSpam implements Listener {
    public static final class AntiSpamConfig {

        /**
         * 最小检测长度
         */
        final int minLength;

        /**
         * 最小的平均字符信息熵
         */
        final double minAverageEntropy;

        /**
         * 最小的整体信息熵
         * <br>
         * 不建议使用此项<br>
         * 无参考性
         */
        final double minEntropy;

        /**
         * 相似度比较-比较的信息数量
         */
        final int similarCheckAmount;

        /**
         * 相似度比较-记录时间长度
         */
        final long similarTime;

        /**
         * 相似度比较-最大相似度 [0.0,1.0]
         */
        final double similarMaxSimilarity;

        /**
         * 相似度比较-最小比较的字符串距离<br>
         * 不建议使用此项<br>
         * 无参考性
         */
        final int similarMinDistance;

        /**
         * 哈希值
         */
        private final int hash;
        public AntiSpamConfig(int minLength, double minAverageEntropy, double minEntropy, int similarCheckAmount,
                              double similarMaxSimilarity, int similarMinDistance, long similarTime) {
            this.minLength = minLength;
            this.minAverageEntropy = minAverageEntropy;
            this.minEntropy = minEntropy;
            this.similarCheckAmount = similarCheckAmount;
            this.similarMaxSimilarity = similarMaxSimilarity;
            this.similarMinDistance = similarMinDistance;
            this.similarTime = similarTime;
            hash = (Integer.toString(minLength) + minAverageEntropy + minEntropy + similarCheckAmount
                    + similarMaxSimilarity + similarMinDistance).hashCode();
        }
        @Override
        public boolean equals(Object obj) {
            if (obj == null || !(obj instanceof AntiSpamConfig)) return false;
            AntiSpamConfig c = (AntiSpamConfig) obj;
            return hash == c.hash &&//
                    minLength == c.minLength &&//
                    minAverageEntropy == c.minAverageEntropy && //
                    minEntropy == c.minEntropy &&//
                    similarCheckAmount == c.similarCheckAmount && //
                    similarMaxSimilarity == c.similarMaxSimilarity &&//
                    similarMinDistance == c.similarMinDistance;
        }

        @Override
        public int hashCode() {
            return hash;
        }
    }
    private static final class MessageCache {
        private static final class TimeString {

            /**
             * 字符串
             */
            final String s;

            /**
             * 时间戳
             */
            final long time;

            /**
             * @param s
             *            字符串
             */
            public TimeString(String s) {
                this.s = s;
                this.time = System.currentTimeMillis();
            }
        }
        /** 当前指针位置 */
        private int now;

        /** 信息缓存 */
        private TimeString[] messages;

        /**
         * 是否已经全覆盖一遍
         */
        private boolean over = false;
        void addMessage(String message, int mesCacheAmount) {
            if (messages == null || messages.length == 0) {
                if (mesCacheAmount == 0) {
                    return;
                } else {
                    messages = new TimeString[mesCacheAmount];
                    now = 0;
                    over = false;
                }
            } else if (mesCacheAmount == 0) {
                messages = null;
                return;
            } else if (messages.length < mesCacheAmount) {
                TimeString[] oldMessages = messages;
                messages = new TimeString[mesCacheAmount];
                for (int i = 0; i < oldMessages.length; i++) {
                    messages[i] = oldMessages[(i + now + 1) % oldMessages.length];
                    // 0 1 2 3 4 5
                    // - - - - - ↑
                    // 0 1 2 3 4 5 6 7
                    // - - - - ↑ - ↑
                }
                now = (oldMessages.length - 1) % mesCacheAmount;
                over = false;
            } else if (messages.length > mesCacheAmount) {
                TimeString[] oldMessages = messages;
                messages = new TimeString[mesCacheAmount];

                for (int i = 0, start = (start = now - mesCacheAmount + 1) > -1 ? start
                        : (start + oldMessages.length); i < mesCacheAmount - 1; i++) {
                    messages[i] = oldMessages[(start + i) % oldMessages.length];
                }
                now = mesCacheAmount - 1;
                over = true;
            }
            messages[now] = new TimeString(message);
            int temp = (now + 1) % messages.length;
            if (temp <= now) over = true;
            now = temp;
        }
    }
    /**
     * 单例
     */
    public static final AntiSpam ME = new AntiSpam();

    /**
     * 基础配置
     * <br>
     * 任何没有特殊配置的玩家均使用此配置
     */
    public static AntiSpamConfig BASE_CONFIG;

    /**
     * 所有的配置
     */
    private static final HashMap<AntiSpamConfig, String> CONFIGS_AS = new LinkedHashMap<>();

    /**
     * 所有的配置
     */
    private static final HashMap<String, AntiSpamConfig> CONFIGS_SA = new LinkedHashMap<>();

    /**
     * 玩家配置
     */
    private static final HashMap<String, AntiSpamConfig> PLAYER_CONFIGS = new HashMap<>();

    /**
     * 玩家聊天记录
     */
    private static final HashMap<String, MessageCache> PLAYER_MESSAGE = new HashMap<>();
    public static boolean addConfig(String key, AntiSpamConfig config) {
        if (key == null || config == null) return false;
        if (CONFIGS_AS.containsKey(config) || CONFIGS_SA.containsKey(key)) return false;
        CONFIGS_AS.put(config, key);
        CONFIGS_SA.put(key, config);
        CONFIG_modCount++;
        return true;
    }
    /**
     * config mod count
     */
    private static int CONFIG_modCount = 0;
    public static int getCONFIGModCount() {
        return CONFIG_modCount;
    }
    public static ArrayList<String> getKeys() {
        return new ArrayList<>(CONFIGS_SA.keySet());
    }
    private static void addMessage(String playerName, String message, int mesCacheAmount) {
        MessageCache cache = PLAYER_MESSAGE.get(playerName);
        if (cache == null) PLAYER_MESSAGE.put(playerName, cache = new MessageCache());
        cache.addMessage(message, mesCacheAmount);
    }

    /** 小于最小信息熵 */
    private static String MES1;

    /** 小于最小平均信息熵 */
    private static String MES2;

    /** 小于最少转换步骤 */
    private static String MES3;

    /** 大于最大相似度 */
    private static String MES4;
    public static int check(String playerName, String message) {
        AntiSpamConfig c = getPlayerConfig(playerName);
        long now = System.currentTimeMillis();
        if (message.length() >= c.minLength) {
            // addMessage(playerName, message, c.similarCheckAmount);
            // return 0;
            if (c.minEntropy != -1) {
                double e = AntiSpamTool.entropy(message);
                if (e < c.minEntropy) return -1;
                if (c.minAverageEntropy != -1) {
                    double ae = AntiSpamTool.averageEntropy(message, e);
                    if (ae < c.minAverageEntropy) return -2;
                }
            } else if (c.minAverageEntropy != -1) {
                double ae = AntiSpamTool.averageEntropy(message);
                if (ae < c.minAverageEntropy) return -2;
            }
        }
        MessageCache cache = PLAYER_MESSAGE.get(playerName);
        if (cache == null) {
            addMessage(playerName, message, c.similarCheckAmount);
            return 0;
        }
        if (c.similarMinDistance != -1) {
            if (c.similarMaxSimilarity != -1) {
                if (cache.over) {
                    for (int i = 0; i < cache.messages.length; i++) {
                        if (now - cache.messages[i].time > c.similarTime) continue;
                        int d = AntiSpamTool.getMinDistance(cache.messages[i].s, message);
                        if (d < c.similarMinDistance) return -3;
                        double s = AntiSpamTool.getSimilarity(cache.messages[i].s, message, d);
                        if (s > c.similarMaxSimilarity) return -4;
                    }
                } else {
                    for (int i = 0; i < cache.now; i++) {
                        if (now - cache.messages[i].time > c.similarTime) continue;
                        int d = AntiSpamTool.getMinDistance(cache.messages[i].s, message);
                        if (d < c.similarMinDistance) return -3;
                        double s = AntiSpamTool.getSimilarity(cache.messages[i].s, message, d);
                        if (s > c.similarMaxSimilarity) return -4;
                    }
                }
            } else {
                if (cache.over) {
                    for (int i = 0; i < cache.messages.length; i++) {
                        if (now - cache.messages[i].time > c.similarTime) continue;
                        int d = AntiSpamTool.getMinDistance(cache.messages[i].s, message);
                        if (d < c.similarMinDistance) return -3;
                    }
                } else {
                    for (int i = 0; i < cache.now; i++) {
                        if (now - cache.messages[i].time > c.similarTime) continue;
                        int d = AntiSpamTool.getMinDistance(cache.messages[i].s, message);
                        if (d < c.similarMinDistance) return -3;
                    }
                }
            }
        } else if (c.similarMaxSimilarity != -1) {
            if (cache.over) {
                for (int i = 0; i < cache.messages.length; i++) {
                    if (now - cache.messages[i].time > c.similarTime) continue;
                    double s = AntiSpamTool.getSimilarity(cache.messages[i].s, message);
                    if (s > c.similarMaxSimilarity) return -4;
                }
            } else {
                for (int i = 0; i < cache.now; i++) {
                    if (now - cache.messages[i].time > c.similarTime) continue;
                    double s = AntiSpamTool.getSimilarity(cache.messages[i].s, message);
                    if (s > c.similarMaxSimilarity) return -4;
                }
            }
        }
        addMessage(playerName, message, c.similarCheckAmount);
        return 0;
    }
    public static AntiSpamConfig getConfig(String key) {
        return CONFIGS_SA.get(key);
    }
    public static String getkey(AntiSpamConfig config) {
        return CONFIGS_AS.get(config);
    }
    public static AntiSpamConfig getPlayerConfig(Player p) {
        AntiSpamConfig config = PLAYER_CONFIGS.get(p.getName());
        return config == null ? BASE_CONFIG : config;
    }
    public static AntiSpamConfig getPlayerConfig(String p) {
        AntiSpamConfig config = PLAYER_CONFIGS.get(p);
        return config == null ? BASE_CONFIG : config;
    }
    public static void load(ConfigSection config, ConfigSection data) {
        MES1 = Main.main().mes("less-than-min-entropy");
        MES2 = Main.main().mes("less-than-min-average-entropy");
        MES3 = Main.main().mes("less-than-min-distance");
        MES4 = Main.main().mes("more-than-max-similarity");
        if (config == null) {
            addConfig("base", BASE_CONFIG = new AntiSpamConfig(5, 0.25, -1, 5, 0.8D, -1, 60000));
        } else {
            ConfigSection section;
            section = config.getSections("base");
            if (section == null)
                addConfig("base", BASE_CONFIG = new AntiSpamConfig(5, 0.25, -1, 5, 0.8D, -1, 60000));
            else
                addConfig("base", BASE_CONFIG = loadFromConfig(section));

            section = config.getSections("black-list");
            if (section != null) addConfig("black-list", loadFromConfig(section));

            section = config.getSections("white-list");
            if (section != null) addConfig("white-list", loadFromConfig(section));
        }
        //
        if (data == null) return;
        ConfigSection section;
        section = data.getSections("configs");
        if (section == null) return;
        for (String key : section.getKeys(false)) {
            addConfig(key, loadFromConfig(section.getSections(key)));
        }
        section = data.getSections("players");
        if (section == null) return;
        for (String key : section.getKeys(false)) {
            AntiSpamConfig asc = getConfig(key);
            if (asc == null) continue;
            PLAYER_CONFIGS.put(key, asc);
        }
    }
    private static AntiSpamConfig loadFromConfig(ConfigSection section) {
        int ml = section.getInt("min-length", 5);
        double mae = section.getDouble("min-average-entropy", -1);
        double me = section.getDouble("min-entropy", -1);
        int sca = section.getInt("similarity.checkAmount", 5);
        double sms = section.getDouble("similarity.max-similarity", -1);
        int smd = section.getInt("similarity.min-distance", -1);
        int st = section.getInt("similarity.time", 60000);
        return new AntiSpamConfig(ml, mae, me, sca, sms, smd, st);
    }
    public static Config saveData() {
        Config yaml = new Config(Config.YAML);
        HashMap<String, String> players = new HashMap<>();
        HashMap<String, Object> configs = new HashMap<>();
        int i = 0;
        for (Map.Entry<String, AntiSpamConfig> e : PLAYER_CONFIGS.entrySet()) {
            String key = getkey(e.getValue());
            if (key == null) key = "@n" + Integer.toString(++i);
            players.put(e.getKey(), key);
            if (key.startsWith("@")) configs.put(key, saveToMap(e.getValue()));
        }
        yaml.set("configs", configs);
        yaml.set("players", players);
        return yaml;
    }
    private static LinkedHashMap<String, Object> saveToMap(AntiSpamConfig c) {
        LinkedHashMap<String, Object> m = new LinkedHashMap<>(), sm = new LinkedHashMap<>();
        m.put("min-length", c.minLength);
        m.put("min-average-entropy", c.minAverageEntropy);
        m.put("min-entropy", c.minEntropy);
        m.put("similarity", sm);
        sm.put("checkAmount", c.similarCheckAmount);
        sm.put("max-similarity", c.similarMaxSimilarity);
        sm.put("min-distance", c.similarMinDistance);
        sm.put("time", c.similarTime);
        return m;

    }
    public static String setPlayerConfig(String playerName, AntiSpamConfig config) {
        if (playerName == null) return null;
        if (config == null) {
            PLAYER_CONFIGS.remove(playerName);
            return "base";
        }
        String key = getkey(config);
        if (key != null) {
            if (key.equals("base"))
                PLAYER_CONFIGS.remove(playerName);
            else
                PLAYER_CONFIGS.put(playerName, getConfig(key));
            return key;
        } else {
            key = "@f" + UUID.randomUUID();
            addConfig(key, config);
            PLAYER_CONFIGS.put(playerName, config);
            return key;
        }
    }
    @Deprecated
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public static void e(PlayerChatEvent e) {
        int back = check(e.getPlayer().getName(), e.getMessage());
        if (back < 0) e.setCancelled(true);

        if (back == -1)//
            e.getPlayer().sendMessage(MES1);
        else if (back == -2)//
            e.getPlayer().sendMessage(MES2);
        else if (back == -3)//
            e.getPlayer().sendMessage(MES3);
        else if (back == -4)//
            e.getPlayer().sendMessage(MES4);
    }
    private AntiSpam() {
    }
}
