package app.Isla4ever;

import java.util.HashMap;

public final class AntiSpamTool {
    private static final class NUM {
        int i;
        private NUM() {
            i = 1;
        }
    }
    public static double averageEntropy(String str) {
        return averageEntropy(str, entropy(str));
    }
    public static double averageEntropy(String str, double entropy) {
        if (str.length() <= 1) return Double.NaN;
        return entropy / Math.log(str.length());
    }
    public static double entropy(String str) {
        if (str.length() <= 1) return 0.0D;
        if (str.length() == 2) {
            if (str.charAt(0) != str.charAt(1))
                return 1.0D;
            else
                return 0.0D;
        }
        double H = .0;
        str = str.toUpperCase(); // 将小写字母转换成大写
        HashMap<Character, NUM> datas = new HashMap<>();
        for (int i = 0; i < str.length(); i++) { // 统计字母个数
            char c = str.charAt(i);
            NUM num = datas.get(c);
            if (num == null)
                datas.put(c, new NUM());
            else
                num.i++;
        }
        // 计算信息熵，将字母出现的频率作为离散概率值
        int len = str.length();
        double log = Math.log(2);
        for (NUM num : datas.values()) {
            double p = 1.0 * num.i / len;// 单个字母的频率
            H -= p * (Math.log(p) / log);// H = -∑Pi*log2(Pi)
        }
        return H;
    }
    public static int getMinDistance(String word1, String word2) {
        int m = word1.length();
        int n = word2.length();
        if (m == 0) return n;
        if (n == 0) return m;
        int[][] f = new int[m + 1][n + 1];
        for (int i = 0; i <= m; i++)
            f[i][0] = i;
        for (int j = 0; j <= n; j++)
            f[0][j] = j;

        for (int i = 1; i <= m; i++)
            for (int j = 1; j <= n; j++)
                if (word1.charAt(i - 1) == word2.charAt(j - 1))
                    f[i][j] = f[i - 1][j - 1];
                else
                    f[i][j] = min(f[i - 1][j - 1], f[i - 1][j], f[i][j - 1]) + 1;

        return f[m][n];
    }
    public static double getSimilarity(String word1, String word2) {
        double distance = getMinDistance(word1, word2);
        return 1 - distance / (word1.length() > word2.length() ? word1.length() : word2.length());
    }
    public static double getSimilarity(String word1, String word2,double distance) {
        return 1 - distance / (word1.length() > word2.length() ? word1.length() : word2.length());
    }
    public static void main(String[] args) {
        System.out.println(Double.doubleToLongBits(0.11));
        // System.out.println(averageEntropy(""));
//        Scanner scan = new Scanner(System.in);
//        String s1 = scan.nextLine(), s2 = scan.nextLine();
//        long __ = System.currentTimeMillis();
//        double g1 = averageEntropy(s1);
//        double g2 = averageEntropy(s2);
//        double g11 = entropy(s1);
//        double g22 = entropy(s2);
//        double g3 = getSimilarity(s1, s2);
//        double g4 = getMinDistance(s1, s2);
//        long ___ = System.currentTimeMillis();
//        System.out.println((___ - __) + "\t" + g1 + "\t" + g2);
//        System.out.println((___ - __) + "\t" + g11 + "\t" + g22);
//        System.out.println("相似:\t" + g3 + "\t" + g4);
//        scan.close();
    }
    private static int min(int a, int b, int c) {
        return (a > b ? (b > c ? c : b) : (a > c ? c : a));
    }
    private AntiSpamTool() {
    }

}
